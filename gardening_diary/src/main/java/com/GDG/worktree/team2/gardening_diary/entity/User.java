package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 사용자 엔티티
 */
@Schema(description = "사용자 정보")
public class User {
    @Schema(description = "사용자 ID", example = "user123")
    @DocumentId
    private String id;
    
    @Schema(description = "Firebase Auth UID", example = "firebase_uid_123")
    private String uid; // Firebase Auth UID
    
    @Schema(description = "아이디 (일반 회원가입용)", example = "testuser")
    private String username; // 아이디 (일반 회원가입용)
    
    @Schema(description = "비밀번호", example = "******", hidden = true)
    private String password; // 비밀번호 (암호화되지 않은 상태)
    
    @Schema(description = "이메일 (구글 로그인용)", example = "user@example.com")
    private String email; // 이메일 (구글 로그인용)
    
    @Schema(description = "닉네임", example = "닉네임")
    private String nickname;
    
    @Schema(description = "표시 이름", example = "홍길동")
    private String displayName;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
    
    @Schema(description = "인증 제공자", example = "USERNAME", allowableValues = {"USERNAME", "GOOGLE"})
    private String authProvider; // "USERNAME" 또는 "GOOGLE"
    
    @Schema(description = "생성 일시", example = "2024-01-01T00:00:00")
    @ServerTimestamp
    private Date createdAt;

    @Schema(description = "수정 일시", example = "2024-01-01T00:00:00")
    @ServerTimestamp
    private Date updatedAt;
    
    // 기본 생성자
    public User() {}
    
    // 생성자
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUid() {
        return uid;
    }
    
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getNickname() {
        return nickname;
    }
    
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public String getAuthProvider() {
        return authProvider;
    }
    
    public void setAuthProvider(String authProvider) {
        this.authProvider = authProvider;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", displayName='" + displayName + '\'' +
                ", profileImageUrl='" + profileImageUrl + '\'' +
                ", authProvider='" + authProvider + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
