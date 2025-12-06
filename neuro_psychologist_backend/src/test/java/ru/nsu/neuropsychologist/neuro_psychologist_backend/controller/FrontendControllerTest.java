package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.FrontendCacheService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FrontendControllerTest {

    @Mock
    private FrontendCacheService frontendCacheService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private FrontendController frontendController;

    private byte[] mockContent;

    @BeforeEach
    void setUp() {
        mockContent = "test content".getBytes();
    }

    @Test
    void testServeIndex_Success() {
        when(frontendCacheService.getFile("/index.html")).thenReturn(mockContent);

        ResponseEntity<byte[]> response = frontendController.serveIndex();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockContent, response.getBody());
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
        verify(frontendCacheService, times(1)).getFile("/index.html");
    }

    @Test
    void testServeIndex_NotFound() {
        when(frontendCacheService.getFile("/index.html")).thenReturn(null);

        ResponseEntity<byte[]> response = frontendController.serveIndex();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(frontendCacheService, times(1)).getFile("/index.html");
    }

    @Test
    void testServeAssets_Success() {
        when(request.getRequestURI()).thenReturn("/assets/main.js");
        when(frontendCacheService.getFile("/assets/main.js")).thenReturn(mockContent);

        ResponseEntity<byte[]> response = frontendController.serveAssets(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockContent, response.getBody());
        verify(frontendCacheService, times(1)).getFile("/assets/main.js");
    }

    @Test
    void testServeSpaRoutes_Success() {
        when(frontendCacheService.getFile("/index.html")).thenReturn(mockContent);

        ResponseEntity<byte[]> response = frontendController.serveSpaRoutes("dashboard");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockContent, response.getBody());
        verify(frontendCacheService, times(1)).getFile("/index.html");
    }

    @Test
    void testServeStaticFile_Success() {
        when(frontendCacheService.getFile("/favicon.ico")).thenReturn(mockContent);

        ResponseEntity<byte[]> response = frontendController.serveStaticFile("favicon.ico");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockContent, response.getBody());
        verify(frontendCacheService, times(1)).getFile("/favicon.ico");
    }

    @Test
    void testServeFile_FallbackToIndex() {
        when(frontendCacheService.getFile("/nonexistent.html")).thenReturn(null);
        when(frontendCacheService.getFile("/index.html")).thenReturn(mockContent);

        ResponseEntity<byte[]> response = frontendController.serveStaticFile("nonexistent.html");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertArrayEquals(mockContent, response.getBody());
        verify(frontendCacheService, times(1)).getFile("/nonexistent.html");
        verify(frontendCacheService, times(1)).getFile("/index.html");
    }

    @Test
    void testServeFile_Exception() {
        when(frontendCacheService.getFile(anyString())).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<byte[]> response = frontendController.serveIndex();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void testGetContentType_Html() {
        when(frontendCacheService.getFile("/index.html")).thenReturn(mockContent);
        
        ResponseEntity<byte[]> response = frontendController.serveIndex();
        
        assertEquals(MediaType.TEXT_HTML, response.getHeaders().getContentType());
    }

    @Test
    void testGetContentType_JavaScript() {
        when(frontendCacheService.getFile("/assets/main.js")).thenReturn(mockContent);
        when(request.getRequestURI()).thenReturn("/assets/main.js");
        
        ResponseEntity<byte[]> response = frontendController.serveAssets(request);
        
        assertEquals(MediaType.valueOf("application/javascript"), response.getHeaders().getContentType());
    }

    @Test
    void testGetContentType_Css() {
        when(frontendCacheService.getFile("/assets/style.css")).thenReturn(mockContent);
        when(request.getRequestURI()).thenReturn("/assets/style.css");
        
        ResponseEntity<byte[]> response = frontendController.serveAssets(request);
        
        assertEquals(MediaType.valueOf("text/css"), response.getHeaders().getContentType());
    }

    @Test
    void testCacheHeaders_Assets() {
        when(frontendCacheService.getFile("/assets/main.js")).thenReturn(mockContent);
        when(request.getRequestURI()).thenReturn("/assets/main.js");
        
        ResponseEntity<byte[]> response = frontendController.serveAssets(request);
        
        String cacheControl = response.getHeaders().getCacheControl();
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("public"));
        assertTrue(cacheControl.contains("max-age=31536000"));
    }

    @Test
    void testCacheHeaders_NonAssets() {
        when(frontendCacheService.getFile("/index.html")).thenReturn(mockContent);
        
        ResponseEntity<byte[]> response = frontendController.serveIndex();
        
        String cacheControl = response.getHeaders().getCacheControl();
        assertNotNull(cacheControl);
        assertTrue(cacheControl.contains("no-cache"));
    }
}