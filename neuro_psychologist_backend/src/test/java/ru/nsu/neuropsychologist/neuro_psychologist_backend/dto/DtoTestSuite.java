package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for all DTO classes
 */
class DtoTestSuite {

    // AuthResponse Tests
    @Test
    void testAuthResponse() {
        AuthResponse response = new AuthResponse();
        assertNotNull(response);
        
        response.setToken("token123");
        response.setEmail("test@example.com");
        response.setFirstName("John");
        response.setUserId(1);
        
        assertEquals("token123", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("John", response.getFirstName());
        assertEquals(1, response.getUserId());
    }

    @Test
    void testAuthResponseConstructor() {
        AuthResponse response = new AuthResponse("token", "email@test.com", "Jane", 2);
        
        assertEquals("token", response.getToken());
        assertEquals("email@test.com", response.getEmail());
        assertEquals("Jane", response.getFirstName());
        assertEquals(2, response.getUserId());
    }

    // LoginRequest Tests
    @Test
    void testLoginRequest() {
        LoginRequest request = new LoginRequest();
        assertNotNull(request);
        
        request.setEmail("user@example.com");
        request.setPassword("password123");
        
        assertEquals("user@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
    }

    @Test
    void testLoginRequestConstructor() {
        LoginRequest request = new LoginRequest("test@example.com", "pass123");
        
        assertEquals("test@example.com", request.getEmail());
        assertEquals("pass123", request.getPassword());
    }

    // RegisterRequest Tests
    @Test
    void testRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        assertNotNull(request);
        
        request.setEmail("new@example.com");
        request.setPassword("password123");
        request.setFirstName("Alice");
        
        assertEquals("new@example.com", request.getEmail());
        assertEquals("password123", request.getPassword());
        assertEquals("Alice", request.getFirstName());
    }

    @Test
    void testRegisterRequestConstructor() {
        RegisterRequest request = new RegisterRequest("test@example.com", "pass123", "Bob");
        
        assertEquals("test@example.com", request.getEmail());
        assertEquals("pass123", request.getPassword());
        assertEquals("Bob", request.getFirstName());
    }

    // CheckInRequest Tests
    @Test
    void testCheckInRequest() {
        CheckInRequest request = new CheckInRequest();
        assertNotNull(request);
        
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        request.setCurrentStateText("Feeling good");
        request.setEnergyMomentsText("Morning walk");
        request.setMissingElementText("More sleep");
        
        assertEquals(4, request.getCalmnessRating());
        assertEquals(3, request.getEnergyRating());
        assertEquals(5, request.getSatisfactionRating());
        assertEquals(2, request.getConnectionRating());
        assertEquals(4, request.getEngagementRating());
        assertEquals("Feeling good", request.getCurrentStateText());
        assertEquals("Morning walk", request.getEnergyMomentsText());
        assertEquals("More sleep", request.getMissingElementText());
    }

    // ChatHistoryResponse Tests
    @Test
    void testChatHistoryResponse() {
        ChatHistoryResponse response = new ChatHistoryResponse();
        assertNotNull(response);
        
        Long id = 1L;
        String userText = "Test text";
        Integer dayRating = 8;
        List<String> recommendations = Arrays.asList("Rec 1", "Rec 2");
        String llmResponse = "LLM response";
        Boolean isCheckin = true;
        ZonedDateTime now = ZonedDateTime.now();
        
        response.setId(id);
        response.setUserText(userText);
        response.setDayRating(dayRating);
        response.setRecommendations(recommendations);
        response.setLlmResponse(llmResponse);
        response.setIsCheckin(isCheckin);
        response.setAnalyzedAt(now);
        response.setCreatedAt(now);
        
        // Check-in fields
        response.setCalmnessRating(4);
        response.setEnergyRating(3);
        response.setSatisfactionRating(5);
        response.setConnectionRating(2);
        response.setEngagementRating(4);
        response.setCurrentStateText("Current state");
        response.setEnergyMomentsText("Energy moments");
        response.setMissingElementText("Missing element");
        
        assertEquals(id, response.getId());
        assertEquals(userText, response.getUserText());
        assertEquals(dayRating, response.getDayRating());
        assertEquals(recommendations, response.getRecommendations());
        assertEquals(llmResponse, response.getLlmResponse());
        assertEquals(isCheckin, response.getIsCheckin());
        assertEquals(now, response.getAnalyzedAt());
        assertEquals(now, response.getCreatedAt());
        
        assertEquals(4, response.getCalmnessRating());
        assertEquals(3, response.getEnergyRating());
        assertEquals(5, response.getSatisfactionRating());
        assertEquals(2, response.getConnectionRating());
        assertEquals(4, response.getEngagementRating());
        assertEquals("Current state", response.getCurrentStateText());
        assertEquals("Energy moments", response.getEnergyMomentsText());
        assertEquals("Missing element", response.getMissingElementText());
    }

    // MetricsResponse Tests
    @Test
    void testMetricsResponse() {
        MetricsResponse response = new MetricsResponse();
        assertNotNull(response);
        
        Long id = 1L;
        ZonedDateTime now = ZonedDateTime.now();
        Boolean isCheckin = false;
        Integer dayRating = 7;
        
        response.setId(id);
        response.setAnalyzedAt(now);
        response.setIsCheckin(isCheckin);
        response.setDayRating(dayRating);
        
        // Check-in ratings
        response.setCalmnessRating(4);
        response.setEnergyRating(3);
        response.setSatisfactionRating(5);
        response.setConnectionRating(2);
        response.setEngagementRating(4);
        
        assertEquals(id, response.getId());
        assertEquals(now, response.getAnalyzedAt());
        assertEquals(isCheckin, response.getIsCheckin());
        assertEquals(dayRating, response.getDayRating());
        assertEquals(4, response.getCalmnessRating());
        assertEquals(3, response.getEnergyRating());
        assertEquals(5, response.getSatisfactionRating());
        assertEquals(2, response.getConnectionRating());
        assertEquals(4, response.getEngagementRating());
    }
}