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
    
    @Column(name = "user_text", columnDefinition = "TEXT", nullable = false)
    private String userText;
    
    @Column(name = "day_rating", nullable = false)
    private Integer dayRating;
    
    @Column(name = "recommendations", columnDefinition = "TEXT")
    private String recommendations;
    
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
}