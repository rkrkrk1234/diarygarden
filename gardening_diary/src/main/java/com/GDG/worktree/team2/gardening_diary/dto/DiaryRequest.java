package com.GDG.worktree.team2.gardening_diary.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 다이어리 요청 DTO
 */
public class DiaryRequest {
    @NotBlank(message = "나무 ID는 필수입니다")
    private String treeId;
    
    @NotBlank(message = "내용은 필수입니다")
    private String content;
    
    // 기본 생성자
    public DiaryRequest() {}
    
    // 생성자
    public DiaryRequest(String treeId, String content) {
        this.treeId = treeId;
        this.content = content;
    }
    
    // Getters and Setters
    public String getTreeId() {
        return treeId;
    }
    
    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    @Override
    public String toString() {
        return "DiaryRequest{" +
                "treeId='" + treeId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}

