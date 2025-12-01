package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.FrontendCacheService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FrontendController {

    private static final Logger logger = LoggerFactory.getLogger(FrontendController.class);

    private final FrontendCacheService frontendCacheService;

    public FrontendController(FrontendCacheService frontendCacheService) {
        this.frontendCacheService = frontendCacheService;
    }

    /**
     * Serve index.html for root path
     */
    @GetMapping("/")
    public ResponseEntity<byte[]> serveIndex() {
        return serveFile("/index.html");
    }

    /**
     * Serve static files (JS, CSS, images, etc.)
     */
    @GetMapping("/assets/**")
    public ResponseEntity<byte[]> serveAssets(HttpServletRequest request) {
        String path = request.getRequestURI();
        return serveFile(path);
    }

    /**
     * Catch-all for SPA routing - serve index.html for any unmatched routes
     * This allows client-side routing to work properly
     */
    @GetMapping("/{path:[^\\.]*}")
    public ResponseEntity<byte[]> serveSpaRoutes(@PathVariable String path) {
        // For SPA routes, always serve index.html
        return serveFile("/index.html");
    }

    /**
     * Serve other static files with extensions (favicon, manifest, etc.)
     */
    @GetMapping("/{filename:.+\\..+}")
    public ResponseEntity<byte[]> serveStaticFile(@PathVariable String filename) {
        return serveFile("/" + filename);
    }

    private ResponseEntity<byte[]> serveFile(String path) {
        try {
            byte[] content = frontendCacheService.getFile(path);
            
            if (content == null) {
                // If file not found and not index.html, try serving index.html for SPA routing
                if (!path.equals("/index.html")) {
                    logger.debug("File not found: {}, trying index.html", path);
                    content = frontendCacheService.getFile("/index.html");
                }
                
                if (content == null) {
                    logger.warn("File not found: {}", path);
                    return ResponseEntity.notFound().build();
                }
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(getContentType(path));
            
            // Add cache headers for static assets
            if (path.startsWith("/assets/")) {
                headers.setCacheControl("public, max-age=31536000, immutable");
            } else {
                headers.setCacheControl("no-cache");
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);
                    
        } catch (Exception e) {
            logger.error("Error serving file: {}", path, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private MediaType getContentType(String path) {
        if (path.endsWith(".html")) {
            return MediaType.TEXT_HTML;
        } else if (path.endsWith(".js")) {
            return MediaType.valueOf("application/javascript");
        } else if (path.endsWith(".css")) {
            return MediaType.valueOf("text/css");
        } else if (path.endsWith(".json")) {
            return MediaType.APPLICATION_JSON;
        } else if (path.endsWith(".png")) {
            return MediaType.IMAGE_PNG;
        } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
            return MediaType.IMAGE_JPEG;
        } else if (path.endsWith(".svg")) {
            return MediaType.valueOf("image/svg+xml");
        } else if (path.endsWith(".ico")) {
            return MediaType.valueOf("image/x-icon");
        } else if (path.endsWith(".woff") || path.endsWith(".woff2")) {
            return MediaType.valueOf("font/woff2");
        } else if (path.endsWith(".ttf")) {
            return MediaType.valueOf("font/ttf");
        } else if (path.endsWith(".webp")) {
            return MediaType.valueOf("image/webp");
        } else if (path.endsWith(".gif")) {
            return MediaType.IMAGE_GIF;
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}