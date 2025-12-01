package com.GDG.worktree.team2.gardening_diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 인증 응답 DTO
 */
@Schema(description = "인증 응답")
public class AuthResponse {
    @Schema(description = "Firebase 인증 토큰", example = "eyJhbGciOiJSUzI1NiIs...")
    private String token;
    
    @Schema(description = "Firebase UID", example = "firebase_uid_123")
    private String uid;
    
    @Schema(description = "이메일", example = "user@example.com")
    private String email;
    
    @Schema(description = "표시 이름", example = "홍길동")
    private String displayName;
    
    @Schema(description = "응답 메시지", example = "인증 성공")
    private String message;
    
    @Schema(description = "성공 여부", example = "true")
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

