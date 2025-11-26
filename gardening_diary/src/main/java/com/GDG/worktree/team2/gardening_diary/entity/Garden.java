package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import java.util.Date;

/**
 * Garden 엔티티
 *
 * <p>필드 구성:
 * <ul>
 *   <li>id: 문서 ID</li>
 *   <li>userId: 사용자 ID</li>
 *   <li>treeCount: 사용자가 보유한 나무 수</li>
 *   <li>createdAt / updatedAt: Firestore 서버 타임스탬프</li>
 * </ul>
 */
public class Garden {

    @DocumentId
    private String id;

    @PropertyName("user_id")
    private String userId;

    @PropertyName("tree_count")
    private int treeCount;

    @PropertyName("created_at")
    @ServerTimestamp
    private Date createdAt;

    @PropertyName("updated_at")
    @ServerTimestamp
    private Date updatedAt;

    public Garden() {}

    public Garden(String userId, int treeCount) {
        this.userId = userId;
        this.treeCount = treeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @PropertyName("user_id")
    public String getUserId() {
        return userId;
    }

    @PropertyName("user_id")
    public void setUserId(String userId) {
        this.userId = userId;
    }

    @PropertyName("tree_count")
    public int getTreeCount() {
        return treeCount;
    }

    @PropertyName("tree_count")
    public void setTreeCount(int treeCount) {
        this.treeCount = treeCount;
    }

    @PropertyName("created_at")
    public Date getCreatedAt() {
        return createdAt;
    }

    @PropertyName("created_at")
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @PropertyName("updated_at")
    public Date getUpdatedAt() {
        return updatedAt;
    }

    @PropertyName("updated_at")
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Garden{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", treeCount=" + treeCount +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
