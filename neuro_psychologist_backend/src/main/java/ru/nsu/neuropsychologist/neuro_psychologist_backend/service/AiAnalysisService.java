package ru.nsu.neuropsychologist.neuro_psychologist_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.config.AiApiProperties;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;

import java.util.List;
import java.util.Map;

@Service
public class AiAnalysisService {

    private final WebClient webClient;
    private final AiApiProperties aiApiProperties;

    @Autowired
    public AiAnalysisService(AiApiProperties aiApiProperties) {
        this.aiApiProperties = aiApiProperties;
        this.webClient = WebClient.builder()
                .baseUrl(aiApiProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Api-Key " + aiApiProperties.getKey())
                .defaultHeader("x-folder-id", aiApiProperties.getFolderId())
                .build();
    }

    public AnalysisResponse analyzeUserText(String userText) {
        return analyzeUserText(new AnalysisRequest(userText));
    }

    public AnalysisResponse analyzeUserText(AnalysisRequest request) {
        try {
            String systemPrompt = getSystemPrompt();
            String userPrompt = createUserPrompt(request.getUserText(), request.getCustomPrompt());
            
            Map<String, Object> requestBody = Map.of(
                "modelUri", aiApiProperties.getModel(),
                "completionOptions", Map.of(
                    "stream", false,
                    "temperature", aiApiProperties.getTemperature(),
                    "maxTokens", String.valueOf(aiApiProperties.getMaxTokens())
                ),
                "messages", List.of(
                    Map.of("role", "system", "text", systemPrompt),
                    Map.of("role", "user", "text", userPrompt)
                )
            );

            Map<String, Object> response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            String analysis = extractAnalysisFromYandexResponse(response);
            return new AnalysisResponse(analysis, true);

        } catch (WebClientResponseException e) {
            return new AnalysisResponse("Ошибка при обращении к AI API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return new AnalysisResponse("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private String getSystemPrompt() {
        String configuredPrompt = aiApiProperties.getSystemPrompt();
        if (configuredPrompt != null && !configuredPrompt.trim().isEmpty()) {
            return configuredPrompt;
        }
        // Default system prompt if not configured
        return "Ты опытный нейропсихолог. Анализируй текст пользователя и предоставь профессиональную психологическую оценку.";
    }

    private String createUserPrompt(String userText, String customPrompt) {
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            // Use custom prompt with user text
            return String.format("%s\n\nТекст для анализа:\n%s", customPrompt, userText);
        }
        
        String promptTemplate = aiApiProperties.getUserPromptTemplate();
        if (promptTemplate != null && !promptTemplate.trim().isEmpty()) {
            // Use configured template
            return String.format(promptTemplate, userText);
        }
        
        // Default prompt template if not configured
        return createPsychologicalAnalysisPrompt(userText);
    }

    private String createPsychologicalAnalysisPrompt(String userText) {
        return String.format(
            "Проанализируй следующий текст с точки зрения нейропсихологии. " +
            "Обрати внимание на эмоциональное состояние, когнитивные паттерны, " +
            "возможные психологические особенности и дай рекомендации:\n\n%s\n\n" +
            "Предоставь структурированный анализ на русском языке.",
            userText
        );
    }

    @SuppressWarnings("unchecked")
    private String extractAnalysisFromYandexResponse(Map<String, Object> response) {
        try {
            Map<String, Object> result = (Map<String, Object>) response.get("result");
            if (result != null) {
                List<Map<String, Object>> alternatives = (List<Map<String, Object>>) result.get("alternatives");
                if (alternatives != null && !alternatives.isEmpty()) {
                    Map<String, Object> firstAlternative = alternatives.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstAlternative.get("message");
                    if (message != null) {
                        return (String) message.get("text");
                    }
                }
            }
            return "Не удалось получить анализ от Yandex GPT API";
        } catch (Exception e) {
            return "Ошибка при обработке ответа от Yandex GPT API: " + e.getMessage();
        }
    }
}