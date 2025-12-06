package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisResponseTest {

    @Test
    void testDefaultConstructor() {
        AnalysisResponse response = new AnalysisResponse();
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getDayRating());
        assertNull(response.getRecommendations());
        assertNull(response.getAnalyzedAt());
        assertFalse(response.isSuccess());
    }

    @Test
    void testConstructorWithRecommendations() {
        List<String> recommendations = Arrays.asList("Rec 1", "Rec 2");
        AnalysisResponse response = new AnalysisResponse(recommendations);
        
        assertEquals(recommendations, response.getRecommendations());
    }

    @Test
    void testConstructorWithRatingAndRecommendations() {
        Integer rating = 8;
        List<String> recommendations = Arrays.asList("Rec 1", "Rec 2");
        ZonedDateTime now = ZonedDateTime.now();
        
        AnalysisResponse response = new AnalysisResponse(rating, recommendations, now);
        
        assertEquals(rating, response.getDayRating());
        assertEquals(recommendations, response.getRecommendations());
        assertEquals(now, response.getAnalyzedAt());
        assertTrue(response.isSuccess());
    }

    @Test
    void testConstructorWithIdRatingAndRecommendations() {
        Long id = 1L;
        Integer rating = 8;
        List<String> recommendations = Arrays.asList("Rec 1", "Rec 2");
        ZonedDateTime now = ZonedDateTime.now();
        
        AnalysisResponse response = new AnalysisResponse(id, rating, recommendations, now);
        
        assertEquals(id, response.getId());
        assertEquals(rating, response.getDayRating());
        assertEquals(recommendations, response.getRecommendations());
        assertEquals(now, response.getAnalyzedAt());
        assertTrue(response.isSuccess());
    }

    @Test
    void testConstructorWithError() {
        String error = "Test error";
        AnalysisResponse response = new AnalysisResponse(error);
        
        assertEquals(error, response.getError());
        assertFalse(response.isSuccess());
    }

    @Test
    void testSettersAndGetters() {
        AnalysisResponse response = new AnalysisResponse();
        
        Long id = 1L;
        Integer rating = 7;
        List<String> recommendations = Arrays.asList("Rec 1", "Rec 2", "Rec 3");
        String analysisText = "Analysis text";
        ZonedDateTime now = ZonedDateTime.now();
        String error = "Error message";
        
        response.setId(id);
        response.setDayRating(rating);
        response.setRecommendations(recommendations);
        response.setAnalysisText(analysisText);
        response.setAnalyzedAt(now);
        response.setSuccess(true);
        response.setError(error);
        
        assertEquals(id, response.getId());
        assertEquals(rating, response.getDayRating());
        assertEquals(recommendations, response.getRecommendations());
        assertEquals(analysisText, response.getAnalysisText());
        assertEquals(now, response.getAnalyzedAt());
        assertTrue(response.isSuccess());
        assertEquals(error, response.getError());
    }
}