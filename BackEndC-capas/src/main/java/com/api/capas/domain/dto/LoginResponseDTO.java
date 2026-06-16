package com.api.capas.domain.dto;

public class LoginResponseDTO {
    private String token;
    private String message;
    private String email;
    public LoginResponseDTO() {}

    public LoginResponseDTO(String token, String message) {
        this.token = token;
        this.message = message;
    }
    
    public LoginResponseDTO(String token, String message, String email) {
        this.token = token;
        this.message = message;
        this.email = email;
    }

    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}