package com.GDG.worktree.team2.gardening_diary.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 정원 요청 DTO
 */
@Schema(description = "정원 생성/수정 요청")
public class GardenRequest {
    @Schema(description = "나무의 개수", example = "5", minimum = "0", required = true)
    @NotNull(message = "나무 수는 필수입니다")
    @Min(value = 0, message = "나무 수는 0 이상이어야 합니다")
    private Integer treeCount;
    
    // 기본 생성자
    public GardenRequest() {}
    
    // 생성자
    public GardenRequest(Integer treeCount) {
        this.treeCount = treeCount;
    }
    
    // Getters and Setters
    public Integer getTreeCount() {
        return treeCount;
    }
    
    public void setTreeCount(Integer treeCount) {
        this.treeCount = treeCount;
    }
    
    @Override
    public String toString() {
        return "GardenRequest{" +
                "treeCount=" + treeCount +
                '}';
    }
}

