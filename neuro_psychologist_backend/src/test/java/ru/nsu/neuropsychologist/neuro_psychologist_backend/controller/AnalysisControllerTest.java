package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

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

    @Test
    void testAnalyzeText_Success() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Тестовый текст для анализа");
        AnalysisResponse mockResponse = new AnalysisResponse(
                8,
                Arrays.asList("Рекомендация 1", "Рекомендация 2"),
                ZonedDateTime.now()
        );
        
        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class)))
                .thenReturn(mockResponse);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("test@example.com");
        
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setEmail("test@example.com");
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
        assertEquals(8, result.getBody().getDayRating());
        
        verify(aiAnalysisService, times(1)).analyzeUserText(any(AnalysisRequest.class));
        verify(dayAnalysisRepository, times(1)).save(any(DayAnalysis.class));
    }

    @Test
    void testAnalyzeText_EmptyText() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("");

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Текст для анализа не может быть пустым", result.getBody().getError());
        
        verify(aiAnalysisService, never()).analyzeUserText(anyString());
    }

    @Test
    void testAnalyzeText_NullText() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest(null);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Текст для анализа не может быть пустым", result.getBody().getError());
        
        verify(aiAnalysisService, never()).analyzeUserText(anyString());
    }

    @Test
    void testAnalyzeText_ServiceError() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Тестовый текст");
        AnalysisResponse mockResponse = new AnalysisResponse("Ошибка сервиса");
        
        when(aiAnalysisService.analyzeUserText(any(AnalysisRequest.class)))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request, null);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Ошибка сервиса", result.getBody().getError());
        
        verify(aiAnalysisService, times(1)).analyzeUserText(any(AnalysisRequest.class));
    }

}