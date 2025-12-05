package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

public class CheckInRequest {
    
    private Integer calmnessRating;
    private Integer energyRating;
    private Integer satisfactionRating;
    private Integer connectionRating;
    private Integer engagementRating;
    
    private String currentStateText;
    private String energyMomentsText;
    private String missingElementText;
    
    public CheckInRequest() {}
    
    // Getters and Setters
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