package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

public class AnalysisRequest {
    private String userText;
    private String customPrompt;

    public AnalysisRequest() {}

    public AnalysisRequest(String userText) {
        this.userText = userText;
    }

    public AnalysisRequest(String userText, String customPrompt) {
        this.userText = userText;
        this.customPrompt = customPrompt;
    }

    public String getUserText() {
        return userText;
    }

    public void setUserText(String userText) {
        this.userText = userText;
    }

    public String getCustomPrompt() {
        return customPrompt;
    }

    public void setCustomPrompt(String customPrompt) {
        this.customPrompt = customPrompt;
    }
}