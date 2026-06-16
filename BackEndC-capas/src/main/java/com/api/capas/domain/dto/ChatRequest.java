package com.api.capas.domain.dto;

public class ChatRequest {
    private String message;

    // Constructor vacío obligatorio para Jackson
    public ChatRequest() {
    }

    // Getter y Setter
    public String getMessage() { 
        return message; 
    }

    public void setMessage(String message) { 
        this.message = message; 
    }
}