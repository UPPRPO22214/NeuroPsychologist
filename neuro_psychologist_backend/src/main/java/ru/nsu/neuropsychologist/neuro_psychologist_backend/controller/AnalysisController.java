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

        if (request.getUserText() == null || request.getUserText().trim().isEmpty()) {
            AnalysisResponse errorResponse = new AnalysisResponse("Текст для анализа не может быть пустым");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Get AI analysis
        AnalysisResponse response = aiAnalysisService.analyzeUserText(request);

//        if (!response.isSuccess()) {
//            return ResponseEntity.internalServerError().body(response);
//        }
//
//        // Save to database if user is authenticated
//        if (authentication != null && authentication.isAuthenticated()) {
//            try {
//                String userEmail = authentication.getName();
//                User user = userRepository.findByEmail(userEmail)
//                        .orElseThrow(() -> new RuntimeException("User not found"));
//
//                DayAnalysis dayAnalysis = new DayAnalysis();
//                dayAnalysis.setUser(user);
//                dayAnalysis.setUserText(request.getUserText());
//                dayAnalysis.setDayRating(response.getDayRating());
//                dayAnalysis.setRecommendations(objectMapper.writeValueAsString(response.getRecommendations()));
//                dayAnalysis.setAnalyzedAt(response.getAnalyzedAt());
//
//                DayAnalysis savedAnalysis = dayAnalysisRepository.save(dayAnalysis);
//                response.setId(savedAnalysis.getId());
//
//            } catch (JsonProcessingException e) {
//                logger.error("Error saving analysis: {}", e.getMessage(), e);
//                return ResponseEntity.internalServerError()
//                        .body(new AnalysisResponse("Ошибка при сохранении анализа: " + e.getMessage()));
//            } catch (Exception e) {
//                logger.error("Unexpected error saving analysis: {}", e.getMessage(), e);
//                return ResponseEntity.internalServerError()
//                        .body(new AnalysisResponse("Ошибка при сохранении анализа: " + e.getMessage()));
//            }
//        }

        return ResponseEntity.ok(response);
    }


}