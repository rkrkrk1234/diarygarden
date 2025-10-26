package com.GDG.worktree.team2.gardening_diary.dto;

/**
 * 인증 응답 DTO
 */
public class AuthResponse {
    private String token;
    private String uid;
    private String email;
    private String displayName;
    private String message;
    private boolean success;
    
    // 기본 생성자
    public AuthResponse() {}
    
    // 성공 생성자
    public AuthResponse(String token, String uid, String email, String displayName) {
        this.token = token;
        this.uid = uid;
        this.email = email;
        this.displayName = displayName;
        this.success = true;
        this.message = "인증 성공";
    }
    
    // 실패 생성자
    public AuthResponse(String message) {
        this.message = message;
        this.success = false;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "token='" + token + '\'' +
                ", uid='" + uid + '\'' +
                ", email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                ", message='" + message + '\'' +
                ", success=" + success +
                '}';
    }
}

