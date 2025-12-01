package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

import java.time.ZonedDateTime;
import java.util.List;

public class AnalysisResponse {
    private Long id;
    private Integer dayRating;
    private List<String> recommendations;
    private ZonedDateTime analyzedAt;
    private boolean success;
    private String error;

    public AnalysisResponse() {}

    public AnalysisResponse(Integer dayRating, List<String> recommendations, ZonedDateTime analyzedAt) {
        this.dayRating = dayRating;
        this.recommendations = recommendations;
        this.analyzedAt = analyzedAt;
        this.success = true;
    }

    public AnalysisResponse(Long id, Integer dayRating, List<String> recommendations, ZonedDateTime analyzedAt) {
        this.id = id;
        this.dayRating = dayRating;
        this.recommendations = recommendations;
        this.analyzedAt = analyzedAt;
        this.success = true;
    }

    public AnalysisResponse(String error) {
        this.error = error;
        this.success = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public ZonedDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(ZonedDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}