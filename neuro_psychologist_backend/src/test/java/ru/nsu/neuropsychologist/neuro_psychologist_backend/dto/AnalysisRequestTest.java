package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisRequestTest {

    @Test
    void testDefaultConstructor() {
        AnalysisRequest request = new AnalysisRequest();
        assertNotNull(request);
        assertNull(request.getUserText());
        assertNull(request.getCustomPrompt());
    }

    @Test
    void testConstructorWithUserText() {
        String userText = "Test text";
        AnalysisRequest request = new AnalysisRequest(userText);
        
        assertEquals(userText, request.getUserText());
        assertNull(request.getCustomPrompt());
    }

    @Test
    void testConstructorWithUserTextAndCustomPrompt() {
        String userText = "Test text";
        String customPrompt = "Custom prompt";
        AnalysisRequest request = new AnalysisRequest(userText, customPrompt);
        
        assertEquals(userText, request.getUserText());
        assertEquals(customPrompt, request.getCustomPrompt());
    }

    @Test
    void testSettersAndGetters() {
        AnalysisRequest request = new AnalysisRequest();
        
        request.setUserText("User text");
        request.setCustomPrompt("Custom prompt");
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        request.setCurrentStateText("Current state");
        request.setEnergyMomentsText("Energy moments");
        request.setMissingElementText("Missing element");
        
        assertEquals("User text", request.getUserText());
        assertEquals("Custom prompt", request.getCustomPrompt());
        assertEquals(4, request.getCalmnessRating());
        assertEquals(3, request.getEnergyRating());
        assertEquals(5, request.getSatisfactionRating());
        assertEquals(2, request.getConnectionRating());
        assertEquals(4, request.getEngagementRating());
        assertEquals("Current state", request.getCurrentStateText());
        assertEquals("Energy moments", request.getEnergyMomentsText());
        assertEquals("Missing element", request.getMissingElementText());
    }

    @Test
    void testIsCheckInRequest_AllRatingsPresent() {
        AnalysisRequest request = new AnalysisRequest();
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        
        assertTrue(request.isCheckInRequest());
    }

    @Test
    void testIsCheckInRequest_MissingRatings() {
        AnalysisRequest request = new AnalysisRequest();
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        // Missing other ratings
        
        assertFalse(request.isCheckInRequest());
    }

    @Test
    void testIsCheckInRequest_NoRatings() {
        AnalysisRequest request = new AnalysisRequest();
        assertFalse(request.isCheckInRequest());
    }
}