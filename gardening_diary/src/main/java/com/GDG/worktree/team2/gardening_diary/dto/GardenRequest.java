package com.GDG.worktree.team2.gardening_diary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * 정원 요청 DTO
 */
public class GardenRequest {
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

