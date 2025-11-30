package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

public class AuthResponse {
    
    private String token;
    private String email;
    private String firstName;
    private Integer userId;
    
    public AuthResponse() {
    }
    
    public AuthResponse(String token, String email, String firstName, Integer userId) {
        this.token = token;
        this.email = email;
        this.firstName = firstName;
        this.userId = userId;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public Integer getUserId() {
        return userId;
    }
    
    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}