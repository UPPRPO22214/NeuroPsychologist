package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AuthResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.LoginRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.RegisterRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.AuthService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void testRegister_Success() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("SecurePass123!");
        request.setFirstName("Иван");

        AuthResponse mockResponse = new AuthResponse(
                "mock.jwt.token",
                "test@example.com",
                "Иван",
                1
        );

        when(authService.register(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> result = authController.register(request);

        // Assert
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof AuthResponse);
        
        AuthResponse response = (AuthResponse) result.getBody();
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("test@example.com", response.getEmail());
        assertEquals("Иван", response.getFirstName());
        assertEquals(Integer.valueOf(1), response.getUserId());

        verify(authService, times(1)).register(request);
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("SecurePass123!");
        request.setFirstName("Иван");

        when(authService.register(request))
                .thenThrow(new RuntimeException("Email already registered"));

        // Act
        ResponseEntity<?> result = authController.register(request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) result.getBody();
        assertEquals("Email already registered", errorMap.get("error"));

        verify(authService, times(1)).register(request);
    }

    @Test
    void testLogin_Success() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("SecurePass123!");

        AuthResponse mockResponse = new AuthResponse(
                "mock.jwt.token",
                "test@example.com",
                "Иван",
                1
        );

        when(authService.login(request)).thenReturn(mockResponse);

        // Act
        ResponseEntity<?> result = authController.login(request);

        // Assert
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof AuthResponse);
        
        AuthResponse response = (AuthResponse) result.getBody();
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("test@example.com", response.getEmail());

        verify(authService, times(1)).login(request);
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("WrongPassword");

        when(authService.login(request))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // Act
        ResponseEntity<?> result = authController.login(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) result.getBody();
        assertEquals("Invalid email or password", errorMap.get("error"));

        verify(authService, times(1)).login(request);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("nonexistent@example.com");
        request.setPassword("SomePassword");

        when(authService.login(request))
                .thenThrow(new RuntimeException("Authentication failed"));

        // Act
        ResponseEntity<?> result = authController.login(request);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertTrue(result.getBody() instanceof Map);
        
        @SuppressWarnings("unchecked")
        Map<String, String> errorMap = (Map<String, String>) result.getBody();
        assertEquals("Invalid email or password", errorMap.get("error"));

        verify(authService, times(1)).login(request);
    }
}