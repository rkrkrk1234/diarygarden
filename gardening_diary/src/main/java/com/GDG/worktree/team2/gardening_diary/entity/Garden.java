package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 정원/나무 엔티티
 */
public class Garden {
    @DocumentId
    private String id;
    
    private String userId; // 사용자 ID
    private String treeType; // 나무 종류 (예: "벚나무")
    private String status; // 상태 (예: "성장 중")
    private int diaryCount; // 다이어리 개수
    private List<String> leafColors; // 잎 색상 목록
    private String treeSnapshot; // 나무 스냅샷 이미지 URL
    private LocalDateTime weekStartDate; // 주 시작 날짜
    private LocalDateTime weekEndDate; // 주 종료 날짜
    
    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
    
    // 기본 생성자
    public Garden() {}
    
    // 생성자
    public Garden(String userId, String treeType) {
        this.userId = userId;
        this.treeType = treeType;
        this.status = "성장 중";
        this.diaryCount = 0;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTreeType() {
        return treeType;
    }
    
    public void setTreeType(String treeType) {
        this.treeType = treeType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getDiaryCount() {
        return diaryCount;
    }
    
    public void setDiaryCount(int diaryCount) {
        this.diaryCount = diaryCount;
    }
    
    public List<String> getLeafColors() {
        return leafColors;
    }
    
    public void setLeafColors(List<String> leafColors) {
        this.leafColors = leafColors;
    }
    
    public String getTreeSnapshot() {
        return treeSnapshot;
    }
    
    public void setTreeSnapshot(String treeSnapshot) {
        this.treeSnapshot = treeSnapshot;
    }
    
    public LocalDateTime getWeekStartDate() {
        return weekStartDate;
    }
    
    public void setWeekStartDate(LocalDateTime weekStartDate) {
        this.weekStartDate = weekStartDate;
    }
    
    public LocalDateTime getWeekEndDate() {
        return weekEndDate;
    }
    
    public void setWeekEndDate(LocalDateTime weekEndDate) {
        this.weekEndDate = weekEndDate;
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
        return "Garden{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", treeType='" + treeType + '\'' +
                ", status='" + status + '\'' +
                ", diaryCount=" + diaryCount +
                ", leafColors=" + leafColors +
                ", treeSnapshot='" + treeSnapshot + '\'' +
                ", weekStartDate=" + weekStartDate +
                ", weekEndDate=" + weekEndDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


