package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 다이어리 엔티티
 */
public class Diary {
    @DocumentId
    private String id;
    
    private String userId; // 사용자 ID
    private String treeId; // 나무 ID (gardens 컬렉션 참조)
    private String content; // 내용
    private LocalDateTime writtenDate; // 작성 날짜
    
    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;
    
    // 기본 생성자
    public Diary() {}
    
    // 생성자
    public Diary(String userId, String treeId, String content) {
        this.userId = userId;
        this.treeId = treeId;
        this.content = content;
        this.writtenDate = LocalDateTime.now();
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
    
    public LocalDateTime getWrittenDate() {
        return writtenDate;
    }
    
    public void setWrittenDate(LocalDateTime writtenDate) {
        this.writtenDate = writtenDate;
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
        return "Diary{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", treeId='" + treeId + '\'' +
                ", content='" + content + '\'' +
                ", writtenDate=" + writtenDate +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
