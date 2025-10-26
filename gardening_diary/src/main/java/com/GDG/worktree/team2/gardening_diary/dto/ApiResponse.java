package com.GDG.worktree.team2.gardening_diary.dto;

/**
 * API 응답 공통 DTO
 */
public class ApiResponse<T> {
    private boolean success;
    private String message;
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

