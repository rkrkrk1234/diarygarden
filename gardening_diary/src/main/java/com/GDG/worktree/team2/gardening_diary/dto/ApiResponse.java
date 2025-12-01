package com.GDG.worktree.team2.gardening_diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * API 응답 공통 DTO
 */
@Schema(description = "API 공통 응답")
public class ApiResponse<T> {
    @Schema(description = "요청 성공 여부", example = "true")
    private boolean success;
    
    @Schema(description = "응답 메시지", example = "정원이 생성/업데이트되었습니다")
    private String message;
    
    @Schema(description = "응답 데이터")
    private T data;
    
    // 기본 생성자
    public ApiResponse() {}
    
    // 성공 응답 생성자
    public ApiResponse(T data) {
        this.success = true;
        this.message = "성공";
        this.data = data;
    }
    
    // 성공 응답 생성자 (메시지 포함)
    public ApiResponse(T data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
    }
    
    // 실패 응답 생성자
    public ApiResponse(String message) {
        this.success = false;
        this.message = message;
        this.data = null;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

