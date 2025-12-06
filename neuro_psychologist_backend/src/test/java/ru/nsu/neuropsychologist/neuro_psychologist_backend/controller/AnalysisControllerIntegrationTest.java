package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.DayAnalysis;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.User;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.repository.DayAnalysisRepository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.repository.UserRepository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.AiAnalysisService;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerIntegrationTest {

    @Mock
    private AiAnalysisService aiAnalysisService;

    @Mock
    private DayAnalysisRepository dayAnalysisRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AnalysisController analysisController;

    private User mockUser;
    private AnalysisResponse mockAnalysisResponse;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");

        mockAnalysisResponse = new AnalysisResponse(
                8,
                Arrays.asList("Recommendation 1", "Recommendation 2"),
                ZonedDateTime.now()
        );
        mockAnalysisResponse.setAnalysisText("Analysis text");
    }

    @Test
    void testAnalyzeText_CheckInRequest_Success() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest();
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        request.setCurrentStateText("Feeling good");
        request.setEnergyMomentsText("Morning walk");
        request.setMissingElementText("More sleep");

        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class)))
                .thenReturn(mockAnalysisResponse);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        
        DayAnalysis savedAnalysis = new DayAnalysis();
        savedAnalysis.setId(1L);
        when(dayAnalysisRepository.save(any(DayAnalysis.class))).thenReturn(savedAnalysis);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, authentication);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        verify(dayAnalysisRepository, times(1)).save(any(DayAnalysis.class));
    }

    @Test
    void testAnalyzeText_CheckInRequest_InvalidRatings() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest();
        request.setCalmnessRating(6); // Invalid - should be 1-5
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        request.setCurrentStateText("Feeling good");
        request.setEnergyMomentsText("Morning walk");
        request.setMissingElementText("More sleep");

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        verify(aiAnalysisService, never()).analyzeUserText(any(AnalysisRequest.class));
    }

    @Test
    void testAnalyzeText_CheckInRequest_MissingTextFields() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest();
        request.setCalmnessRating(4);
        request.setEnergyRating(3);
        request.setSatisfactionRating(5);
        request.setConnectionRating(2);
        request.setEngagementRating(4);
        request.setCurrentStateText(""); // Empty
        request.setEnergyMomentsText("Morning walk");
        request.setMissingElementText("More sleep");

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        verify(aiAnalysisService, never()).analyzeUserText(any(AnalysisRequest.class));
    }

    @Test
    void testAnalyzeText_UnauthenticatedUser() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Test text");
        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class)))
                .thenReturn(mockAnalysisResponse);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(dayAnalysisRepository, never()).save(any());
    }

    @Test
    void testAnalyzeText_SaveError() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Test text");
        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class)))
                .thenReturn(mockAnalysisResponse);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(dayAnalysisRepository.save(any(DayAnalysis.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
    }

    @Test
    void testGetChatHistory_Success() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        
        DayAnalysis analysis1 = new DayAnalysis();
        analysis1.setId(1L);
        analysis1.setUserText("Text 1");
        analysis1.setDayRating(8);
        analysis1.setAnalyzedAt(ZonedDateTime.now());
        analysis1.setCreatedAt(ZonedDateTime.now());
        analysis1.setIsCheckin(false);
        
        when(dayAnalysisRepository.findByUserOrderByAnalyzedAtDesc(any(), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Arrays.asList(analysis1)));

        // Act
        ResponseEntity<?> result = analysisController.getChatHistory(0, 20, authentication);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof Map);
    }

    @Test
    void testGetChatHistory_Unauthorized() {
        // Act
        ResponseEntity<?> result = analysisController.getChatHistory(0, 20, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void testGetChatHistory_Error() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> result = analysisController.getChatHistory(0, 20, authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void testGetMetrics_Success() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        
        DayAnalysis analysis = new DayAnalysis();
        analysis.setId(1L);
        analysis.setDayRating(8);
        analysis.setAnalyzedAt(ZonedDateTime.now());
        analysis.setIsCheckin(false);
        
        when(dayAnalysisRepository.findByUserAndAnalyzedAtBetweenOrderByAnalyzedAtDesc(any(), any(), any()))
                .thenReturn(Arrays.asList(analysis));

        // Act
        ResponseEntity<?> result = analysisController.getMetrics(null, null, authentication);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof List);
    }

    @Test
    void testGetMetrics_WithDateRange() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        when(dayAnalysisRepository.findByUserAndAnalyzedAtBetweenOrderByAnalyzedAtDesc(any(), any(), any()))
                .thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> result = analysisController.getMetrics("2024-01-01", "2024-01-31", authentication);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void testGetMetrics_Unauthorized() {
        // Act
        ResponseEntity<?> result = analysisController.getMetrics(null, null, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
    }

    @Test
    void testGetMetrics_Error() {
        // Arrange
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail(anyString())).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> result = analysisController.getMetrics(null, null, authentication);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    void testAnalyzeText_WithRecommendations() throws Exception {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Test text");
        AnalysisResponse response = new AnalysisResponse(
                7,
                Arrays.asList("Rec 1", "Rec 2", "Rec 3"),
                ZonedDateTime.now()
        );
        response.setAnalysisText("Analysis");
        
        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class))).thenReturn(response);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(mockUser));
        
        DayAnalysis savedAnalysis = new DayAnalysis();
        savedAnalysis.setId(1L);
        when(dayAnalysisRepository.save(any(DayAnalysis.class))).thenReturn(savedAnalysis);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, authentication);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(dayAnalysisRepository, times(1)).save(any(DayAnalysis.class));
    }
}