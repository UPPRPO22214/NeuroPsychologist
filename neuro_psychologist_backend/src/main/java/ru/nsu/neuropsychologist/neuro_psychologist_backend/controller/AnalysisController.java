package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.DayAnalysis;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.entity.User;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.repository.DayAnalysisRepository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.repository.UserRepository;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.AiAnalysisService;

import java.util.List;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AiAnalysisService aiAnalysisService;
    private final DayAnalysisRepository dayAnalysisRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(AnalysisController.class);


    @Autowired
    public AnalysisController(
            AiAnalysisService aiAnalysisService,
            DayAnalysisRepository dayAnalysisRepository,
            UserRepository userRepository) {
        this.aiAnalysisService = aiAnalysisService;
        this.dayAnalysisRepository = dayAnalysisRepository;
        this.userRepository = userRepository;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeText(
            @RequestBody AnalysisRequest request,
            Authentication authentication) {

        logger.info("Received analysis request. Is check-in: {}", request.isCheckInRequest());

        // Validate request based on type
        if (request.isCheckInRequest()) {
            // Validate check-in data
            if (!isValidCheckInRequest(request)) {
                AnalysisResponse errorResponse = new AnalysisResponse("Пожалуйста, заполните все поля чекапа");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        } else {
            // Validate regular text analysis
            if (request.getUserText() == null || request.getUserText().trim().isEmpty()) {
                AnalysisResponse errorResponse = new AnalysisResponse("Текст для анализа не может быть пустым");
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }

        // Get AI analysis (service will automatically detect check-in vs regular analysis)
        AnalysisResponse response = aiAnalysisService.analyzeUserText(request);

        if (!response.isSuccess()) {
            return ResponseEntity.internalServerError().body(response);
        }

        // Save to database if user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String userEmail = authentication.getName();
                User user = userRepository.findByEmail(userEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"));

                DayAnalysis dayAnalysis = new DayAnalysis();
                dayAnalysis.setUser(user);
                dayAnalysis.setAnalyzedAt(response.getAnalyzedAt());
                
                // Set LLM response
                dayAnalysis.setLlmResponse(response.getAnalysisText());
                
                // Check if this is a check-in request
                if (request.isCheckInRequest()) {
                    dayAnalysis.setIsCheckin(true);
                    
                    // Save check-in ratings
                    dayAnalysis.setCalmnessRating(request.getCalmnessRating());
                    dayAnalysis.setEnergyRating(request.getEnergyRating());
                    dayAnalysis.setSatisfactionRating(request.getSatisfactionRating());
                    dayAnalysis.setConnectionRating(request.getConnectionRating());
                    dayAnalysis.setEngagementRating(request.getEngagementRating());
                    
                    // Save check-in text responses
                    dayAnalysis.setCurrentStateText(request.getCurrentStateText());
                    dayAnalysis.setEnergyMomentsText(request.getEnergyMomentsText());
                    dayAnalysis.setMissingElementText(request.getMissingElementText());
                } else {
                    dayAnalysis.setIsCheckin(false);
                    
                    // Save regular analysis data
                    dayAnalysis.setUserText(request.getUserText());
                    dayAnalysis.setDayRating(response.getDayRating());
                }
                
                // Save recommendations as JSON if present
                if (response.getRecommendations() != null && !response.getRecommendations().isEmpty()) {
                    dayAnalysis.setRecommendations(objectMapper.writeValueAsString(response.getRecommendations()));
                }

                DayAnalysis savedAnalysis = dayAnalysisRepository.save(dayAnalysis);
                response.setId(savedAnalysis.getId());
                
                logger.info("Successfully saved analysis with ID: {}", savedAnalysis.getId());

            } catch (JsonProcessingException e) {
                logger.error("Error saving analysis: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError()
                        .body(new AnalysisResponse("Ошибка при сохранении анализа: " + e.getMessage()));
            } catch (Exception e) {
                logger.error("Unexpected error saving analysis: {}", e.getMessage(), e);
                return ResponseEntity.internalServerError()
                        .body(new AnalysisResponse("Ошибка при сохранении анализа: " + e.getMessage()));
            }
        }

        return ResponseEntity.ok(response);
    }

    private boolean isValidCheckInRequest(AnalysisRequest request) {
        // Check that all ratings are present and valid (1-5)
        if (request.getCalmnessRating() == null ||
            request.getCalmnessRating() < 1 ||
            request.getCalmnessRating() > 5) {
            return false;
        }
        if (request.getEnergyRating() == null ||
            request.getEnergyRating() < 1 ||
            request.getEnergyRating() > 5) {
            return false;
        }
        if (request.getSatisfactionRating() == null ||
            request.getSatisfactionRating() < 1 ||
            request.getSatisfactionRating() > 5) {
            return false;
        }
        if (request.getConnectionRating() == null ||
            request.getConnectionRating() < 1 ||
            request.getConnectionRating() > 5) {
            return false;
        }
        if (request.getEngagementRating() == null ||
            request.getEngagementRating() < 1 ||
            request.getEngagementRating() > 5) {
            return false;
        }

        // Check that text answers are present
        if (request.getCurrentStateText() == null || request.getCurrentStateText().trim().isEmpty()) {
            return false;
        }
        if (request.getEnergyMomentsText() == null || request.getEnergyMomentsText().trim().isEmpty()) {
            return false;
        }
        if (request.getMissingElementText() == null || request.getMissingElementText().trim().isEmpty()) {
            return false;
        }

        return true;
    }

}