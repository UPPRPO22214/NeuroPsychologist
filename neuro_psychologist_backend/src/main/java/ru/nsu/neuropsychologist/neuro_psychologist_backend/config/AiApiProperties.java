package ru.nsu.neuropsychologist.neuro_psychologist_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ai.api")
public class AiApiProperties {
    private String url;
    private String key;
    private String model;
    private int maxTokens;
    private double temperature;
    
    // Yandex Cloud specific properties
    private String folderId;
    private String keyId;
    
    // Prompt configuration
    private String systemPrompt;
    private String userPromptTemplate;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
    
    public String getFolderId() {
        return folderId;
    }
    
    public void setFolderId(String folderId) {
        this.folderId = folderId;
    }
    
    public String getKeyId() {
        return keyId;
    }
    
    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
    
    public String getSystemPrompt() {
        return systemPrompt;
    }
    
    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }
    
    public String getUserPromptTemplate() {
        return userPromptTemplate;
    }
    
    public void setUserPromptTemplate(String userPromptTemplate) {
        this.userPromptTemplate = userPromptTemplate;
    }
}