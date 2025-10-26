package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.AiAnalysisService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnalysisControllerTest {

    @Mock
    private AiAnalysisService aiAnalysisService;

    @InjectMocks
    private AnalysisController analysisController;

    @Test
    void testAnalyzeText_Success() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("Тестовый текст для анализа");
        AnalysisResponse mockResponse = new AnalysisResponse("Анализ текста", true);
        
        when(aiAnalysisService.analyzeUserText("Тестовый текст для анализа"))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody().isSuccess());
        assertEquals("Анализ текста", result.getBody().getAnalysis());
        
        verify(aiAnalysisService, times(1)).analyzeUserText("Тестовый текст для анализа");
    }

    @Test
    void testAnalyzeText_EmptyText() {
        // Arrange
        AnalysisRequest request = new AnalysisRequest("");

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request);

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
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request);

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
        
        when(aiAnalysisService.analyzeUserText("Тестовый текст"))
                .thenReturn(mockResponse);

        // Act
        ResponseEntity<AnalysisResponse> result = analysisController.analyzeText(request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertNotNull(result.getBody());
        assertFalse(result.getBody().isSuccess());
        assertEquals("Ошибка сервиса", result.getBody().getError());
        
        verify(aiAnalysisService, times(1)).analyzeUserText("Тестовый текст");
    }

    @Test
    void testHealthCheck() {
        // Act
        ResponseEntity<String> result = analysisController.healthCheck();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Сервис анализа работает", result.getBody());
    }
}