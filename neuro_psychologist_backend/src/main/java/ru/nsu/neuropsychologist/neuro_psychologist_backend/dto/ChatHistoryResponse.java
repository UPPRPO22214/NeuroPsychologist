package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import java.time.ZonedDateTime;
import java.util.List;

public class ChatHistoryResponse {
    
    private Long id;
    private String userText;
    private Integer dayRating;
    private List<String> recommendations;
    private String llmResponse;
    private Boolean isCheckin;
    private ZonedDateTime analyzedAt;
    private ZonedDateTime createdAt;
    
    // Check-in specific fields
    private Integer calmnessRating;
    private Integer energyRating;
    private Integer satisfactionRating;
    private Integer connectionRating;
    private Integer engagementRating;
    private String currentStateText;
    private String energyMomentsText;
    private String missingElementText;
    
    public ChatHistoryResponse() {
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserText() {
        return userText;
    }
    
    public void setUserText(String userText) {
        this.userText = userText;
    }
    
    public Integer getDayRating() {
        return dayRating;
    }
    
    public void setDayRating(Integer dayRating) {
        this.dayRating = dayRating;
    }
    
    public List<String> getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(List<String> recommendations) {
        this.recommendations = recommendations;
    }
    
    public String getLlmResponse() {
        return llmResponse;
    }
    
    public void setLlmResponse(String llmResponse) {
        this.llmResponse = llmResponse;
    }
    
    public Boolean getIsCheckin() {
        return isCheckin;
    }
    
    public void setIsCheckin(Boolean isCheckin) {
        this.isCheckin = isCheckin;
    }
    
    public ZonedDateTime getAnalyzedAt() {
        return analyzedAt;
    }
    
    public void setAnalyzedAt(ZonedDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
    
    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Integer getCalmnessRating() {
        return calmnessRating;
    }
    
    public void setCalmnessRating(Integer calmnessRating) {
        this.calmnessRating = calmnessRating;
    }
    
    public Integer getEnergyRating() {
        return energyRating;
    }
    
    public void setEnergyRating(Integer energyRating) {
        this.energyRating = energyRating;
    }
    
    public Integer getSatisfactionRating() {
        return satisfactionRating;
    }
    
    public void setSatisfactionRating(Integer satisfactionRating) {
        this.satisfactionRating = satisfactionRating;
    }
    
    public Integer getConnectionRating() {
        return connectionRating;
    }
    
    public void setConnectionRating(Integer connectionRating) {
        this.connectionRating = connectionRating;
    }
    
    public Integer getEngagementRating() {
        return engagementRating;
    }
    
    public void setEngagementRating(Integer engagementRating) {
        this.engagementRating = engagementRating;
    }
    
    public String getCurrentStateText() {
        return currentStateText;
    }
    
    public void setCurrentStateText(String currentStateText) {
        this.currentStateText = currentStateText;
    }
    
    public String getEnergyMomentsText() {
        return energyMomentsText;
    }
    
    public void setEnergyMomentsText(String energyMomentsText) {
        this.energyMomentsText = energyMomentsText;
    }
    
    public String getMissingElementText() {
        return missingElementText;
    }
    
    public void setMissingElementText(String missingElementText) {
        this.missingElementText = missingElementText;
    }
}