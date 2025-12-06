package ru.nsu.neuropsychologist.neuro_psychologist_backend.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiApiPropertiesTest {

    @Test
    void testAiApiPropertiesCreation() {
        AiApiProperties properties = new AiApiProperties();
        assertNotNull(properties);
    }

    @Test
    void testSettersAndGetters() {
        AiApiProperties properties = new AiApiProperties();
        
        properties.setUrl("https://api.example.com");
        properties.setKey("test-key");
        properties.setModel("gpt-4");
        properties.setMaxTokens(2000);
        properties.setTemperature(0.7);
        properties.setFolderId("folder-123");
        properties.setKeyId("key-456");
        properties.setSystemPrompt("System prompt");
        properties.setUserPromptTemplate("User prompt: %s");
        
        assertEquals("https://api.example.com", properties.getUrl());
        assertEquals("test-key", properties.getKey());
        assertEquals("gpt-4", properties.getModel());
        assertEquals(2000, properties.getMaxTokens());
        assertEquals(0.7, properties.getTemperature());
        assertEquals("folder-123", properties.getFolderId());
        assertEquals("key-456", properties.getKeyId());
        assertEquals("System prompt", properties.getSystemPrompt());
        assertEquals("User prompt: %s", properties.getUserPromptTemplate());
    }

    @Test
    void testDefaultValues() {
        AiApiProperties properties = new AiApiProperties();
        
        assertNull(properties.getUrl());
        assertNull(properties.getKey());
        assertNull(properties.getModel());
        assertEquals(0, properties.getMaxTokens());
        assertEquals(0.0, properties.getTemperature());
        assertNull(properties.getFolderId());
        assertNull(properties.getKeyId());
        assertNull(properties.getSystemPrompt());
        assertNull(properties.getUserPromptTemplate());
    }
}