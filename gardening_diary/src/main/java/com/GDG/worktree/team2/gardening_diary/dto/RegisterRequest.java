package com.GDG.worktree.team2.gardening_diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청 DTO
 */
@Schema(description = "회원가입 요청")
public class RegisterRequest {
    @Schema(description = "아이디 (4-20자, 영문/숫자/언더스코어만 가능)", example = "testuser", required = true, minLength = 4, maxLength = 20)
    @NotBlank(message = "아이디는 필수입니다")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "아이디는 영문, 숫자, 언더스코어만 사용 가능합니다")
    private String username;
    
    @Schema(description = "비밀번호 (최소 6자 이상)", example = "password123", required = true, minLength = 6)
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 6, message = "비밀번호는 최소 6자 이상이어야 합니다")
    private String password;
    
    @Schema(description = "표시 이름", example = "홍길동", required = true)
    @NotBlank(message = "이름은 필수입니다")
    private String displayName;
    
    // 기본 생성자
    public RegisterRequest() {}
    
    // 생성자
    public RegisterRequest(String username, String password, String displayName) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
    }
    
    // Getters and Setters
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
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}

