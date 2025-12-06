package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import java.time.ZonedDateTime;

public class MetricsResponse {
    
    private Long id;
    private ZonedDateTime analyzedAt;
    private Boolean isCheckin;
    
    // Check-in metrics
    private Integer calmnessRating;
    private Integer energyRating;
    private Integer satisfactionRating;
    private Integer connectionRating;
    private Integer engagementRating;
    
    // Regular analysis metrics
    private Integer dayRating;
    
    public MetricsResponse() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public ZonedDateTime getAnalyzedAt() {
        return analyzedAt;
    }
    
    public void setAnalyzedAt(ZonedDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
    
    public Boolean getIsCheckin() {
        return isCheckin;
    }
    
    public void setIsCheckin(Boolean isCheckin) {
        this.isCheckin = isCheckin;
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
    
    public Integer getDayRating() {
        return dayRating;
    }
    
    public void setDayRating(Integer dayRating) {
        this.dayRating = dayRating;
    }
}