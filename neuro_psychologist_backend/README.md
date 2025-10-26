# Нейропсихолог Backend

Это Spring Boot приложение для анализа текста пользователя с помощью AI API.

## Функциональность

Приложение предоставляет REST API endpoint, который:
1. Принимает текст от пользователя
2. Отправляет его в AI API (OpenAI) с промптом для нейропсихологического анализа
3. Возвращает анализ пользователю

## Требования

- Java 17 или выше
- Maven 3.6 или выше
- API ключ от OpenAI

## Установка и запуск

1. Установите Java 17:
   ```bash
   # На macOS с Homebrew
   brew install openjdk@17
   
   # Или скачайте с официального сайта Oracle/OpenJDK
   ```

2. Клонируйте репозиторий и перейдите в директорию проекта

3. Установите переменную окружения с API ключом:
   ```bash
   export AI_API_KEY=your-openai-api-key-here
   ```

4. Соберите и запустите приложение:
   ```bash
   ./mvnw clean spring-boot:run
   ```

## API Endpoints

### POST /api/analysis/analyze
Анализирует текст пользователя

**Запрос:**
```json
{
  "userText": "Ваш текст для анализа"
}
```

**Ответ (успешный):**
```json
{
  "analysis": "Детальный нейропсихологический анализ...",
  "success": true,
  "error": null
}
```

**Ответ (ошибка):**
```json
{
  "analysis": null,
  "success": false,
  "error": "Описание ошибки"
}
```

### GET /api/analysis/health
Проверка работоспособности сервиса

**Ответ:**
```
Сервис анализа работает
```

## Конфигурация

Настройки находятся в `src/main/resources/application.properties`:

```properties
# AI API Configuration
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=${AI_API_KEY:your-api-key-here}
ai.api.model=gpt-3.5-turbo
ai.api.max-tokens=1000
ai.api.temperature=0.7

# Server Configuration
server.port=8080
```

## Пример использования

```bash
# Проверка работоспособности
curl http://localhost:8080/api/analysis/health

# Анализ текста
curl -X POST http://localhost:8080/api/analysis/analyze \
  -H "Content-Type: application/json" \
  -d '{"userText": "Я чувствую себя очень уставшим в последнее время и не могу сосредоточиться на работе."}'
```

## Структура проекта

```
src/main/java/ru/nsu/neuropsychologist/neuro_psychologist_backend/
├── NeuroPsychologistBackendApplication.java  # Главный класс приложения
├── config/
│   └── AiApiProperties.java                  # Конфигурация AI API
├── controller/
│   └── AnalysisController.java               # REST контроллер
├── dto/
│   ├── AnalysisRequest.java                  # DTO для запроса
│   └── AnalysisResponse.java                 # DTO для ответа
└── service/
    └── AiAnalysisService.java                # Сервис для работы с AI API