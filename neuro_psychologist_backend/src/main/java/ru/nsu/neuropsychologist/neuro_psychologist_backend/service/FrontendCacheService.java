package ru.nsu.neuropsychologist.neuro_psychologist_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FrontendCacheService {

    private static final Logger logger = LoggerFactory.getLogger(FrontendCacheService.class);

    private final WebClient webClient;
    private final String frontendBaseUrl;
    private final Path cacheDirectory;
    private final Map<String, byte[]> fileCache = new ConcurrentHashMap<>();

    public FrontendCacheService(
            WebClient.Builder webClientBuilder,
            @Value("${frontend.base-url:https://storage.yandexcloud.net/neuropsychologist-front/dist}") String frontendBaseUrl,
            @Value("${frontend.cache-dir:./frontend-cache}") String cacheDir) {
        this.webClient = webClientBuilder.baseUrl(frontendBaseUrl).build();
        this.frontendBaseUrl = frontendBaseUrl;
        this.cacheDirectory = Paths.get(cacheDir);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(cacheDirectory);
            logger.info("Frontend cache directory initialized at: {}", cacheDirectory.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create cache directory", e);
        }
    }

    /**
     * Get file from cache or download it
     */
    public byte[] getFile(String path) {
        // Normalize path
        String normalizedPath = normalizePath(path);
        
        // Check memory cache first
        byte[] cachedContent = fileCache.get(normalizedPath);
        if (cachedContent != null) {
            logger.debug("Serving {} from memory cache", normalizedPath);
            return cachedContent;
        }

        // Check disk cache
        Path cachedFile = getCachedFilePath(normalizedPath);
        if (Files.exists(cachedFile)) {
            try {
                byte[] content = Files.readAllBytes(cachedFile);
                fileCache.put(normalizedPath, content);
                logger.debug("Serving {} from disk cache", normalizedPath);
                return content;
            } catch (IOException e) {
                logger.warn("Failed to read cached file: {}", cachedFile, e);
            }
        }

        // Download from Yandex Cloud
        try {
            byte[] content = downloadFile(normalizedPath);
            if (content != null) {
                // Save to disk cache
                saveToDiskCache(normalizedPath, content);
                // Save to memory cache
                fileCache.put(normalizedPath, content);
                logger.info("Downloaded and cached: {}", normalizedPath);
                return content;
            }
        } catch (Exception e) {
            logger.error("Failed to download file: {}", normalizedPath, e);
        }

        return null;
    }

    private String normalizePath(String path) {
        if (path == null || path.isEmpty() || path.equals("/")) {
            return "/index.html";
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    private byte[] downloadFile(String path) {
        try {
            return webClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
        } catch (Exception e) {
            logger.error("Failed to download file from {}{}", frontendBaseUrl, path, e);
            return null;
        }
    }

    private Path getCachedFilePath(String path) {
        // Remove leading slash and create path
        String relativePath = path.startsWith("/") ? path.substring(1) : path;
        return cacheDirectory.resolve(relativePath);
    }

    private void saveToDiskCache(String path, byte[] content) {
        try {
            Path cachedFile = getCachedFilePath(path);
            Files.createDirectories(cachedFile.getParent());
            Files.write(cachedFile, content);
            logger.debug("Saved to disk cache: {}", cachedFile);
        } catch (IOException e) {
            logger.warn("Failed to save file to disk cache: {}", path, e);
        }
    }

    /**
     * Clear all caches
     */
    public void clearCache() {
        fileCache.clear();
        try {
            Files.walk(cacheDirectory)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Files.delete(file);
                        } catch (IOException e) {
                            logger.warn("Failed to delete cached file: {}", file, e);
                        }
                    });
            logger.info("Cache cleared");
        } catch (IOException e) {
            logger.error("Failed to clear disk cache", e);
        }
    }

    /**
     * Preload common files
     */
    public void preloadCommonFiles() {
        String[] commonFiles = {"/index.html", "/favicon.ico", "/manifest.json"};
        for (String file : commonFiles) {
            try {
                getFile(file);
            } catch (Exception e) {
                logger.warn("Failed to preload file: {}", file, e);
            }
        }
    }
}