package ru.nsu.neuropsychologist.neuro_psychologist_backend.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.User;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Создаём JwtUtil и устанавливаем значения через Reflection
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret",
            "testSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L); 
        
        testUser = new User();
        testUser.setId(1);
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Иван");
        testUser.setPasswordHash("hashedPassword");
    }

    @Test
    void testGenerateToken_Success() {
        // Act
        String token = jwtUtil.generateToken(testUser);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT имеет 3 части: header.payload.signature
    }

    @Test
    void testExtractUsername_Success() {
        // Arrange
        String token = jwtUtil.generateToken(testUser);

        // Act
        String username = jwtUtil.extractUsername(token);

        // Assert
        assertEquals("test@example.com", username);
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String token = jwtUtil.generateToken(testUser);

        // Act
        boolean isValid = jwtUtil.validateToken(token, testUser);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void testValidateToken_WrongUser() {
        // Arrange
        String token = jwtUtil.generateToken(testUser);
        
        User differentUser = new User();
        differentUser.setId(2);
        differentUser.setEmail("different@example.com");
        differentUser.setFirstName("Петр");
        differentUser.setPasswordHash("hashedPassword");

        // Act
        boolean isValid = jwtUtil.validateToken(token, differentUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void testValidateToken_ExpiredToken() {
        // Arrange
        // Создаём JwtUtil с очень коротким временем жизни токена
        JwtUtil shortLivedJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortLivedJwtUtil, "secret",
                "testSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLongForHS256Algorithm");
        ReflectionTestUtils.setField(shortLivedJwtUtil, "expiration", 1L); // 1 миллисекунда
        
        String token = shortLivedJwtUtil.generateToken(testUser);
        
        // Ждём, чтобы токен истёк
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Thread sleep interrupted");
        }

        // Act & Assert
        assertThrows(ExpiredJwtException.class, () -> {
            shortLivedJwtUtil.validateToken(token, testUser);
        });
    }

    @Test
    void testExtractUsername_InvalidToken() {
        // Arrange
        String invalidToken = "invalid.jwt.token";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtUtil.extractUsername(invalidToken);
        });
    }

    @Test
    void testGenerateToken_DifferentUsers() {
        // Arrange
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("user1@example.com");
        user1.setFirstName("Иван");
        user1.setPasswordHash("hash1");

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("user2@example.com");
        user2.setFirstName("Петр");
        user2.setPasswordHash("hash2");

        // Act
        String token1 = jwtUtil.generateToken(user1);
        String token2 = jwtUtil.generateToken(user2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals("user1@example.com", jwtUtil.extractUsername(token1));
        assertEquals("user2@example.com", jwtUtil.extractUsername(token2));
    }

    @Test
    void testTokenContainsCorrectClaims() {
        // Arrange & Act
        String token = jwtUtil.generateToken(testUser);
        String extractedEmail = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(testUser.getEmail(), extractedEmail);
        assertEquals(testUser.getUsername(), extractedEmail); // Username должен быть равен email
    }
}