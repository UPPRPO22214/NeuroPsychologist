package ru.nsu.neuropsychologist.neuro_psychologist_backend.entity;

import jakarta.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "day_analyses", indexes = {
    @Index(name = "idx_day_analyses_user_id", columnList = "user_id"),
    @Index(name = "idx_day_analyses_analyzed_at", columnList = "analyzed_at")
})
public class DayAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_text", columnDefinition = "TEXT")
    private String userText;
    
    @Column(name = "day_rating")
    private Integer dayRating;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
    // Check-in fields
    @Column(name = "calmness_rating")
    private Integer calmnessRating;
    
    @Column(name = "energy_rating")
    private Integer energyRating;
    
    @Column(name = "satisfaction_rating")
    private Integer satisfactionRating;
    
    @Column(name = "connection_rating")
    private Integer connectionRating;
    
    @Column(name = "engagement_rating")
    private Integer engagementRating;
    
    @Column(name = "current_state_text", columnDefinition = "TEXT")
    private String currentStateText;
    
    @Column(name = "energy_moments_text", columnDefinition = "TEXT")
    private String energyMomentsText;
    
    @Column(name = "missing_element_text", columnDefinition = "TEXT")
    private String missingElementText;
    
    @Column(name = "llm_response", columnDefinition = "TEXT")
    private String llmResponse;
    
    @Column(name = "is_checkin")
    private Boolean isCheckin = false;
    
    @Column(name = "analyzed_at", nullable = false)
    private ZonedDateTime analyzedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
        if (analyzedAt == null) {
            analyzedAt = ZonedDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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
    
    public String getRecommendations() {
        return recommendations;
    }
    
    public void setRecommendations(String recommendations) {
        this.recommendations = recommendations;
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
    
    // Check-in getters and setters
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
}