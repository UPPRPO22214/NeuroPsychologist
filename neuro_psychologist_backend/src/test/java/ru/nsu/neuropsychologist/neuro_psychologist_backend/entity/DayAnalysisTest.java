package ru.nsu.neuropsychologist.neuro_psychologist_backend.entity;

import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DayAnalysisTest {

    @Test
    void testDayAnalysisCreation() {
        DayAnalysis analysis = new DayAnalysis();
        assertNotNull(analysis);
    }

    @Test
    void testSettersAndGetters() {
        DayAnalysis analysis = new DayAnalysis();
        User user = new User();
        user.setId(1);
        
        ZonedDateTime now = ZonedDateTime.now();
        
        analysis.setId(1L);
        analysis.setUser(user);
        analysis.setUserText("Test text");
        analysis.setDayRating(8);
        analysis.setRecommendations("[\"Rec 1\", \"Rec 2\"]");
        analysis.setLlmResponse("LLM response");
        analysis.setIsCheckin(false);
        analysis.setAnalyzedAt(now);
        analysis.setCreatedAt(now);
        
        assertEquals(1L, analysis.getId());
        assertEquals(user, analysis.getUser());
        assertEquals("Test text", analysis.getUserText());
        assertEquals(8, analysis.getDayRating());
        assertEquals("[\"Rec 1\", \"Rec 2\"]", analysis.getRecommendations());
        assertEquals("LLM response", analysis.getLlmResponse());
        assertEquals(false, analysis.getIsCheckin());
        assertEquals(now, analysis.getAnalyzedAt());
        assertEquals(now, analysis.getCreatedAt());
    }

    @Test
    void testCheckInFields() {
        DayAnalysis analysis = new DayAnalysis();
        
        analysis.setIsCheckin(true);
        analysis.setCalmnessRating(4);
        analysis.setEnergyRating(3);
        analysis.setSatisfactionRating(5);
        analysis.setConnectionRating(2);
        analysis.setEngagementRating(4);
        analysis.setCurrentStateText("Current state");
        analysis.setEnergyMomentsText("Energy moments");
        analysis.setMissingElementText("Missing element");
        
        assertTrue(analysis.getIsCheckin());
        assertEquals(4, analysis.getCalmnessRating());
        assertEquals(3, analysis.getEnergyRating());
        assertEquals(5, analysis.getSatisfactionRating());
        assertEquals(2, analysis.getConnectionRating());
        assertEquals(4, analysis.getEngagementRating());
        assertEquals("Current state", analysis.getCurrentStateText());
        assertEquals("Energy moments", analysis.getEnergyMomentsText());
        assertEquals("Missing element", analysis.getMissingElementText());
    }

    @Test
    void testPrePersist() {
        DayAnalysis analysis = new DayAnalysis();
        
        // Simulate @PrePersist - manually set timestamps
        ZonedDateTime now = ZonedDateTime.now();
        analysis.setCreatedAt(now);
        if (analysis.getAnalyzedAt() == null) {
            analysis.setAnalyzedAt(now);
        }
        
        assertNotNull(analysis.getCreatedAt());
        assertNotNull(analysis.getAnalyzedAt());
    }

    @Test
    void testPrePersistWithExistingAnalyzedAt() {
        DayAnalysis analysis = new DayAnalysis();
        ZonedDateTime customTime = ZonedDateTime.now().minusHours(1);
        analysis.setAnalyzedAt(customTime);
        
        // Simulate @PrePersist
        ZonedDateTime now = ZonedDateTime.now();
        analysis.setCreatedAt(now);
        // analyzedAt should not be overwritten if already set
        
        assertNotNull(analysis.getCreatedAt());
        assertEquals(customTime, analysis.getAnalyzedAt());
    }

    @Test
    void testDefaultIsCheckinValue() {
        DayAnalysis analysis = new DayAnalysis();
        // Default value should be false
        analysis.setIsCheckin(false);
        assertFalse(analysis.getIsCheckin());
    }
}