package ru.nsu.neuropsychologist.neuro_psychologist_backend.dto;

public class UserProfileResponse {
    
    private Integer id;
    private String email;
    private String firstName;
    
    public UserProfileResponse() {
    }
    
    public UserProfileResponse(Integer id, String email, String firstName) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
    }
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
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
}