package com.GDG.worktree.team2.gardening_diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * 다이어리 요청 DTO
 */
@Schema(description = "다이어리 생성 요청")
public class DiaryRequest {
    @Schema(description = "나무 ID", example = "tree123", required = true)
    @NotBlank(message = "나무 ID는 필수입니다")
    private String treeId;
    
    @Schema(description = "다이어리 내용", example = "오늘은 정원에 물을 주었습니다.", required = true)
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

