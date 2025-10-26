package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

public class AnalysisResponse {
    private String analysis;
    private boolean success;
    private String error;

    public AnalysisResponse() {}

    public AnalysisResponse(String analysis, boolean success) {
        this.analysis = analysis;
        this.success = success;
    }

    public AnalysisResponse(String error) {
        this.error = error;
        this.success = false;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
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