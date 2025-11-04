package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HealthControllerTest {

    @InjectMocks
    private HealthController healthController;

    @Test
    void testHealthCheck() {
        // Act
        ResponseEntity<String> result = healthController.healthCheck();

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Сервис анализа работает", result.getBody());
    }
}