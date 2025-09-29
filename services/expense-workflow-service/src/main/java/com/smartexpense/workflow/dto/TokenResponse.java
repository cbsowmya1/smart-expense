// src/main/java/com/smartexpense/workflow/dto/TokenResponse.java
package com.smartexpense.workflow.dto;

public class TokenResponse {
    private String token;
    private String role;
    private long expiresAt;
    
    public TokenResponse() {}
    
    public TokenResponse(String token) {
        this.token = token;
    }


    public TokenResponse(String token, String role, long expiresAt) {
        this.token = token; this.role = role; this.expiresAt = expiresAt;
    }
    public String getToken() { return token; }
    public String getRole() { return role; }
    public long getExpiresAt() { return expiresAt; }
}
