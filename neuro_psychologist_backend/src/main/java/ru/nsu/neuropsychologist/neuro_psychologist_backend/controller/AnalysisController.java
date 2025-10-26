package ru.nsu.neuropsychologist.neuro_psychologist_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.service.AiAnalysisService;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class AnalysisController {

    private final AiAnalysisService aiAnalysisService;

    @Autowired
    public AnalysisController(AiAnalysisService aiAnalysisService) {
        this.aiAnalysisService = aiAnalysisService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyzeText(@RequestBody AnalysisRequest request) {
        if (request.getUserText() == null || request.getUserText().trim().isEmpty()) {
            AnalysisResponse errorResponse = new AnalysisResponse("Текст для анализа не может быть пустым");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        AnalysisResponse response = aiAnalysisService.analyzeUserText(request.getUserText());
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Сервис анализа работает");
    }
}