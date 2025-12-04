package ru.nsu.neuropsychologist.neuro_psychologist_backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.config.AiApiProperties;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisRequest;
import ru.nsu.neuropsychologist.neuro_psychologist_backend.dto.AnalysisResponse;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AiAnalysisService.class);
    private static final Pattern JSON_PATTERN = Pattern.compile("\\{.*?\\}", Pattern.DOTALL);
    private static final Pattern RATING_PATTERN = Pattern.compile("(\\d+)/10|(\\d+)\\s*из\\s*10|рейтинг[\\s:]*([1-9]|10)");
    private static final Pattern RECOMMENDATION_PATTERN = Pattern.compile(
            "(?:рекомендация|совет)[\\s:\\d]*[\\-\\•\\*]?\\s*(.+?)(?=\\n|$|(?:рекомендация|совет))",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private final WebClient.Builder webClientBuilder;
    private final AiApiProperties aiApiProperties;
    private final YandexIamTokenService iamTokenService;

    @Autowired
    public AiAnalysisService(AiApiProperties aiApiProperties, YandexIamTokenService iamTokenService) {
        this.aiApiProperties = aiApiProperties;
        this.iamTokenService = iamTokenService;
        this.webClientBuilder = WebClient.builder()
                .baseUrl(aiApiProperties.getUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("x-folder-id", aiApiProperties.getFolderId());
    }

    public AnalysisResponse analyzeUserText(String userText) {
        return analyzeUserText(new AnalysisRequest(userText));
    }

    public AnalysisResponse analyzeUserText(AnalysisRequest request) {
        try {
            logger.info("Starting analysis for user text");

            // Check if this is a check-in request
            if (request.isCheckInRequest()) {
                logger.info("Processing as check-in request");
                return analyzeCheckIn(request);
            }

            // Проверка на минимальную осмысленность текста
            if (!isTextValidForAnalysis(request.getUserText())) {
                return createInvalidTextResponse(request.getUserText());
            }

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

            logger.info("Getting IAM token");
            String iamToken = iamTokenService.getIamToken();
            logger.info("IAM token obtained successfully");

            WebClient webClient = webClientBuilder
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + iamToken)
                    .build();

            logger.info("Sending request to Yandex GPT API");
            String responseJson = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received response from Yandex GPT API");
            logger.debug("Raw response: {}", responseJson);

            // Извлекаем текст из JSON ответа
            String extractedText = extractTextFromYandexResponse(responseJson);
            logger.info("Extracted text from response");

            // Создаем ответ с извлеченным текстом
            AnalysisResponse response = new AnalysisResponse();
            response.setAnalysisText(extractedText);
            response.setSuccess(true);
            response.setAnalyzedAt(ZonedDateTime.now());
            
            return response;

        } catch (WebClientResponseException e) {
            logger.error("WebClient error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new AnalysisResponse("Ошибка при обращении к AI API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error during analysis", e);
            return new AnalysisResponse("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private AnalysisResponse parseTextResponse(String responseJson) {
        try {
            // Извлекаем текстовый ответ из JSON структуры
            String textResponse = extractTextFromYandexResponse(responseJson);
            logger.info("Extracted text response: {}", textResponse);

            // Пытаемся найти JSON в тексте
            Integer dayRating = extractDayRating(textResponse);
            List<String> recommendations = extractRecommendations(textResponse);

            // Если не удалось извлечь структурированные данные, используем текст как рекомендацию
            if (dayRating == null && recommendations.isEmpty()) {
                logger.info("No structured data found, using text as recommendation");
                recommendations = List.of(textResponse);
                dayRating = estimateRatingFromText(textResponse);
            }

            // Если всё равно нет рейтинга, ставим по умолчанию
            if (dayRating == null) {
                dayRating = 5; // средний рейтинг по умолчанию
            }

            // Создаем успешный ответ
            AnalysisResponse response = new AnalysisResponse();
            response.setDayRating(dayRating);
            response.setRecommendations(recommendations);
            response.setAnalyzedAt(ZonedDateTime.now());
            response.setSuccess(true);

            return response;

        } catch (Exception e) {
            logger.error("Error parsing AI response: {}", e.getMessage(), e);
            return new AnalysisResponse("Ошибка при обработке ответа AI: " + e.getMessage());
        }
    }

    private String extractTextFromYandexResponse(String responseJson) {
        try {
            // Пытаемся парсить как JSON
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            var root = mapper.readTree(responseJson);
            var result = root.path("result");
            var alternatives = result.path("alternatives");

            if (alternatives.isArray() && !alternatives.isEmpty()) {
                var firstAlternative = alternatives.get(0);
                var message = firstAlternative.path("message");
                return message.path("text").asText();
            }
        } catch (Exception e) {
            logger.warn("Could not parse as JSON, using raw response");
        }

        // Если не удалось парсить JSON, ищем текст вручную
        return findTextInResponse(responseJson);
    }

    private String findTextInResponse(String response) {
        // Ищем текстовый контент в ответе
        if (response.contains("\"text\"")) {
            int textStart = response.indexOf("\"text\":\"") + 8;
            if (textStart > 8) {
                int textEnd = response.indexOf("\"", textStart);
                if (textEnd > textStart) {
                    return response.substring(textStart, textEnd).replace("\\n", "\n");
                }
            }
        }

        // Если не нашли структурированный текст, возвращаем как есть
        return response;
    }

    private Integer extractDayRating(String text) {
        // Ищем рейтинг 1-10 в тексте
        Matcher matcher = RATING_PATTERN.matcher(text.toLowerCase());
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                if (matcher.group(i) != null) {
                    try {
                        int rating = Integer.parseInt(matcher.group(i));
                        if (rating >= 1 && rating <= 10) {
                            return rating;
                        }
                    } catch (NumberFormatException e) {
                        // Продолжаем поиск
                    }
                }
            }
        }

        // Ищем просто числа 1-10
        Pattern simpleNumber = Pattern.compile("\\b([1-9]|10)\\b");
        matcher = simpleNumber.matcher(text);
        if (matcher.find()) {
            try {
                int rating = Integer.parseInt(matcher.group(1));
                if (rating >= 1 && rating <= 10) {
                    return rating;
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        return null;
    }

    private List<String> extractRecommendations(String text) {
        List<String> recommendations = new ArrayList<>();

        // Ищем рекомендации в тексте
        Matcher matcher = RECOMMENDATION_PATTERN.matcher(text);
        while (matcher.find()) {
            String recommendation = matcher.group(1).trim();
            if (!recommendation.isEmpty() && recommendation.length() > 10) {
                recommendations.add(recommendation);
            }
        }

        // Если не нашли по паттерну, разбиваем текст на абзацы
        if (recommendations.isEmpty()) {
            String[] paragraphs = text.split("\\n\\n|\\.\\s+");
            for (String paragraph : paragraphs) {
                String trimmed = paragraph.trim();
                if (trimmed.length() > 20 && !trimmed.toLowerCase().contains("рейтинг")) {
                    recommendations.add(trimmed);
                }
            }
        }

        // Ограничиваем количество рекомендаций
        if (recommendations.size() > 3) {
            recommendations = recommendations.subList(0, 3);
        }

        return recommendations;
    }

    private Integer estimateRatingFromText(String text) {
        // Простая эвристика для оценки настроения по тексту
        String lowerText = text.toLowerCase();

        int positiveWords = countOccurrences(lowerText,
                "хорош", "положительн", "отличн", "замечательн", "прекрасн", "радост", "счастлив");
        int negativeWords = countOccurrences(lowerText,
                "плох", "отрицательн", "ужасн", "грустн", "печальн", "депресси", "тревожн");

        if (positiveWords > negativeWords * 2) return 8;
        if (positiveWords > negativeWords) return 6;
        if (negativeWords > positiveWords * 2) return 3;
        if (negativeWords > positiveWords) return 4;

        return 5; // нейтральный
    }

    private int countOccurrences(String text, String... words) {
        int count = 0;
        for (String word : words) {
            Pattern pattern = Pattern.compile("\\b" + word + "\\w*\\b");
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }

    private boolean isTextValidForAnalysis(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        // Проверяем на бессмысленный текст (одни буквы без пробелов)
        String trimmed = text.trim();
        if (trimmed.length() < 10) {
            return false;
        }

        // Проверяем наличие пробелов и знаков препинания
        boolean hasSpaces = trimmed.contains(" ");
        boolean hasPunctuation = trimmed.matches(".*[.,!?;:].*");

        // Проверяем на повторяющиеся символы или бессмысленный набор
        if (!hasSpaces && trimmed.length() < 20) {
            return false;
        }

        return true;
    }

    private AnalysisResponse createInvalidTextResponse(String text) {
        AnalysisResponse response = new AnalysisResponse();
        response.setSuccess(false);
        response.setError("Текст слишком короткий или не содержит достаточно информации для анализа. " +
                "Пожалуйста, опишите ваш день более подробно.");
        response.setDayRating(5); // Средний рейтинг по умолчанию
        response.setRecommendations(List.of(
                "Попробуйте описать ваш день более подробно: что произошло, какие эмоции вы испытывали",
                "Опишите ваши мысли и чувства в течение дня",
                "Расскажите о взаимодействиях с другими людьми"
        ));
        response.setAnalyzedAt(ZonedDateTime.now());
        return response;
    }

    public AnalysisResponse analyzeCheckIn(AnalysisRequest request) {
        try {
            logger.info("Starting check-in analysis");

            // Combine all check-in data into structured text
            String combinedText = buildCheckInText(request);
            
            String systemPrompt = getCheckInSystemPrompt();
            String userPrompt = combinedText;

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

            logger.info("Getting IAM token");
            String iamToken = iamTokenService.getIamToken();
            logger.info("IAM token obtained successfully");

            WebClient webClient = webClientBuilder
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + iamToken)
                    .build();

            logger.info("Sending check-in request to Yandex GPT API");
            String responseJson = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("Received response from Yandex GPT API");
            logger.debug("Raw response: {}", responseJson);

            // Extract text from JSON response
            String extractedText = extractTextFromYandexResponse(responseJson);
            logger.info("Extracted text from response");

            // Parse recommendations from the response
            List<String> recommendations = extractRecommendationsFromText(extractedText);

            // Create response
            AnalysisResponse response = new AnalysisResponse();
            response.setAnalysisText(extractedText);
            response.setRecommendations(recommendations);
            response.setSuccess(true);
            response.setAnalyzedAt(ZonedDateTime.now());
            
            return response;

        } catch (WebClientResponseException e) {
            logger.error("WebClient error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return new AnalysisResponse("Ошибка при обращении к AI API: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
        } catch (Exception e) {
            logger.error("Unexpected error during check-in analysis", e);
            return new AnalysisResponse("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }

    private String buildCheckInText(AnalysisRequest request) {
        StringBuilder text = new StringBuilder();
        
        text.append("=== ЧЕКАП ДНЯ ===\n\n");
        
        text.append("ОЦЕНКИ ПО ШКАЛЕ ОТ 1 ДО 5:\n\n");
        
        text.append("1. Спокойствие и гармония с собой: ")
            .append(request.getCalmnessRating()).append("/5\n");
        
        text.append("2. Энергия (наполненность vs истощение): ")
            .append(request.getEnergyRating()).append("/5\n");
        
        text.append("3. Удовлетворённость днём: ")
            .append(request.getSatisfactionRating()).append("/5\n");
        
        text.append("4. Близость и теплота контактов с людьми: ")
            .append(request.getConnectionRating()).append("/5\n");
        
        text.append("5. Интерес, радость, вовлечённость: ")
            .append(request.getEngagementRating()).append("/5\n\n");
        
        text.append("ОТКРЫТЫЕ ОТВЕТЫ:\n\n");
        
        text.append("Описание текущего состояния (слово/образ/метафора):\n")
            .append(request.getCurrentStateText()).append("\n\n");
        
        text.append("Моменты, которые отняли/добавили энергии:\n")
            .append(request.getEnergyMomentsText()).append("\n\n");
        
        text.append("Чего не хватает для полного покоя или удовлетворения:\n")
            .append(request.getMissingElementText()).append("\n");
        
        return text.toString();
    }

    private String getCheckInSystemPrompt() {
        return "Ты опытный нейропсихолог, специализирующийся на анализе эмоционального состояния и психологического благополучия. " +
                "Пользователь прошёл чекап дня, где оценил различные аспекты своего состояния по шкале от 1 до 5 и ответил на открытые вопросы.\n\n" +
                "Твоя задача:\n" +
                "1. Проанализировать общую картину дня пользователя на основе его оценок и ответов\n" +
                "2. Выявить ключевые паттерны и области, требующие внимания\n" +
                "3. Дать 3-5 конкретных, практических рекомендаций для улучшения психологического состояния\n\n" +
                "Формат ответа:\n" +
                "- Начни с краткого обзора состояния пользователя (2-3 предложения)\n" +
                "- Затем дай рекомендации в формате нумерованного списка\n" +
                "- Рекомендации должны быть конкретными, выполнимыми и учитывать контекст ответов пользователя\n" +
                "- Используй тёплый, поддерживающий тон";
    }

    private List<String> extractRecommendationsFromText(String text) {
        List<String> recommendations = new ArrayList<>();
        
        // Try to find numbered recommendations
        Pattern numberedPattern = Pattern.compile("(?:^|\\n)\\s*\\d+\\.\\s*(.+?)(?=\\n\\s*\\d+\\.|\\n\\n|$)", Pattern.DOTALL);
        Matcher matcher = numberedPattern.matcher(text);
        
        while (matcher.find()) {
            String recommendation = matcher.group(1).trim();
            if (!recommendation.isEmpty() && recommendation.length() > 10) {
                recommendations.add(recommendation);
            }
        }
        
        // If no numbered recommendations found, try bullet points
        if (recommendations.isEmpty()) {
            Pattern bulletPattern = Pattern.compile("(?:^|\\n)\\s*[•\\-\\*]\\s*(.+?)(?=\\n\\s*[•\\-\\*]|\\n\\n|$)", Pattern.DOTALL);
            matcher = bulletPattern.matcher(text);
            
            while (matcher.find()) {
                String recommendation = matcher.group(1).trim();
                if (!recommendation.isEmpty() && recommendation.length() > 10) {
                    recommendations.add(recommendation);
                }
            }
        }
        
        return recommendations;
    }

    private String getSystemPrompt() {
        String configuredPrompt = aiApiProperties.getSystemPrompt();
        if (configuredPrompt != null && !configuredPrompt.trim().isEmpty()) {
            return configuredPrompt;
        }
        return "Ты опытный нейропсихолог. Анализируй текст пользователя о его дне и предоставь профессиональную психологическую оценку. " +
                "Дай оценку дня от 1 до 10 (где 1 - очень плохой день, 10 - отличный день) и 3 конкретные рекомендации по улучшению состояния.";
    }

    private String createUserPrompt(String userText, String customPrompt) {
        if (customPrompt != null && !customPrompt.trim().isEmpty()) {
            return String.format("%s\n\nТекст для анализа:\n%s", customPrompt, userText);
        }

        String promptTemplate = aiApiProperties.getUserPromptTemplate();
        if (promptTemplate != null && !promptTemplate.trim().isEmpty()) {
            return String.format(promptTemplate, userText);
        }

        return createPsychologicalAnalysisPrompt(userText);
    }

    private String createPsychologicalAnalysisPrompt(String userText) {
        return String.format(
                "Проанализируй следующий текст пользователя о его дне с точки зрения нейропсихологии:\n\n" +
                        "Текст пользователя:\n%s\n\n" +
                        "Сделай следующее:\n" +
                        "1. Оцени день пользователя по шкале от 1 до 10 (где 1 - очень плохой день, 10 - отличный день)\n" +
                        "2. Дай 3 конкретные практические рекомендации по восстановлению и улучшению психологического состояния\n\n" +
                        "Ответ дай в свободной текстовой форме.",
                userText
        );
    }
}