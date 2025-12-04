package ru.nsu.neuropsychologist.neuro_psychologist_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Service
public class YandexIamTokenService {
    
    private static final Logger logger = LoggerFactory.getLogger(YandexIamTokenService.class);
    private static final String IAM_TOKEN_URL = "https://iam.api.cloud.yandex.net/iam/v1/tokens";
    private static final String SA_KEY_FILE = "./sa-key.json";
    private static final long TOKEN_LIFETIME_SECONDS = 3600; // 1 hour
    
    private String cachedIamToken;
    private Instant tokenExpirationTime;
    private final ObjectMapper objectMapper;
    private final WebClient webClient;
    
    public YandexIamTokenService() {
        this.objectMapper = new ObjectMapper();
        this.webClient = WebClient.builder().build();
    }
    
    public synchronized String getIamToken() {
        if (cachedIamToken != null && tokenExpirationTime != null &&
            Instant.now().isBefore(tokenExpirationTime.minusSeconds(300))) {
            logger.debug("Using cached IAM token");
            return cachedIamToken;
        }
        
        try {
            logger.info("Generating new IAM token");
            String jwtToken = createJwtToken();
            logger.debug("JWT token created successfully");
            cachedIamToken = exchangeJwtForIamToken(jwtToken);
            tokenExpirationTime = Instant.now().plusSeconds(TOKEN_LIFETIME_SECONDS);
            logger.info("IAM token obtained and cached successfully");
            return cachedIamToken;
        } catch (Exception e) {
            logger.error("Failed to get IAM token", e);
            throw new RuntimeException("Failed to get IAM token: " + e.getMessage(), e);
        }
    }
    
    private String createJwtToken() throws Exception {
        logger.debug("Loading service account key");
        JsonNode saKey = loadServiceAccountKey();
        
        String keyId = saKey.get("id").asText();
        String serviceAccountId = saKey.get("service_account_id").asText();
        String privateKeyPem = saKey.get("private_key").asText();
        
        logger.debug("Parsing private key");
        PrivateKey privateKey = parsePrivateKey(privateKeyPem);
        
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(3600);
        
        return Jwts.builder()
                .setIssuer(serviceAccountId)
                .setAudience(IAM_TOKEN_URL)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .setHeaderParam("kid", keyId)
                .signWith(privateKey, SignatureAlgorithm.PS256)
                .compact();
    }
    
    private String exchangeJwtForIamToken(String jwtToken) {
        try {
            Map<String, Object> requestBody = Map.of("jwt", jwtToken);
            
            Map<String, Object> response = webClient.post()
                    .uri(IAM_TOKEN_URL)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            
            if (response != null && response.containsKey("iamToken")) {
                return (String) response.get("iamToken");
            }
            
            throw new RuntimeException("No IAM token in response");
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange JWT for IAM token: " + e.getMessage(), e);
        }
    }
    
    private JsonNode loadServiceAccountKey() throws IOException {
        File keyFile = new File(SA_KEY_FILE);
        if (!keyFile.exists()) {
            String absolutePath = keyFile.getAbsolutePath();
            throw new IOException("Service account key file not found at: " + absolutePath +
                    ". Please ensure sa-key.json is in the project root directory.");
        }
        return objectMapper.readTree(keyFile);
    }
    
    private PrivateKey parsePrivateKey(String privateKeyPem) throws Exception {
        // Remove header, footer, and Yandex comment line
        String[] lines = privateKeyPem.split("\\n");
        StringBuilder keyContent = new StringBuilder();
        
        for (String line : lines) {
            line = line.trim();
            // Skip header, footer, and comment lines
            if (line.isEmpty() ||
                line.contains("BEGIN PRIVATE KEY") ||
                line.contains("END PRIVATE KEY") ||
                line.contains("PLEASE DO NOT REMOVE")) {
                continue;
            }
            keyContent.append(line);
        }
        
        logger.debug("Parsed private key length: {}", keyContent.length());
        byte[] keyBytes = Base64.getDecoder().decode(keyContent.toString());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
}