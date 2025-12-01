package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.PropertyName;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * Tree 엔티티
 *
 * <p>필드 구성:
 * <ul>
 *   <li>id: 문서 ID</li>
 *   <li>userId: 사용자 ID</li>
 *   <li>weekShortDate: 주 시작 날짜</li>
 *   <li>weekEndDate: 주 종료 날짜</li>
 *   <li>diaryLeafColors: 일기 잎 색상들</li>
 *   <li>treeSnapshot: 나무 스냅샷</li>
 *   <li>status: 상태</li>
 *   <li>createdAt / updatedAt: Firestore 서버 타임스탬프</li>
 * </ul>
 */
@Schema(description = "나무 정보")
public class Tree {

    @Schema(description = "나무 ID", example = "tree123")
    @DocumentId
    private String id;

    @Schema(description = "사용자 ID", example = "user123")
    @PropertyName("user_id")
    private String userId;

    @Schema(description = "주 시작 날짜", example = "2024-01-01T00:00:00")
    @PropertyName("week_short_date")
    private Date weekShortDate;

    @Schema(description = "주 종료 날짜", example = "2024-01-07T23:59:59")
    @PropertyName("week_end_date")
    private Date weekEndDate;

    @Schema(description = "다이어리 잎 색상들", example = "green,yellow,red")
    @PropertyName("diary_leaf_colors")
    private String diaryLeafColors;

    @Schema(description = "나무 스냅샷", example = "base64_encoded_image")
    @PropertyName("tree_snapshot")
    private String treeSnapshot;

    @Schema(description = "나무 상태", example = "active", allowableValues = {"active", "inactive", "completed"})
    @PropertyName("status")
    private String status;

    @Schema(description = "생성 일시", example = "2024-01-01T00:00:00")
    @PropertyName("created_at")
    @ServerTimestamp
    private Date createdAt;

    @Schema(description = "수정 일시", example = "2024-01-01T00:00:00")
    @PropertyName("updated_at")
    @ServerTimestamp
    private Date updatedAt;

    public Tree() {}

    public Tree(String userId, Date weekShortDate, Date weekEndDate, String diaryLeafColors, String treeSnapshot, String status) {
        this.userId = userId;
        this.weekShortDate = weekShortDate;
        this.weekEndDate = weekEndDate;
        this.diaryLeafColors = diaryLeafColors;
        this.treeSnapshot = treeSnapshot;
        this.status = status;
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

    @PropertyName("week_short_date")
    public Date getWeekShortDate() {
        return weekShortDate;
    }

    @PropertyName("week_short_date")
    public void setWeekShortDate(Date weekShortDate) {
        this.weekShortDate = weekShortDate;
    }

    @PropertyName("week_end_date")
    public Date getWeekEndDate() {
        return weekEndDate;
    }

    @PropertyName("week_end_date")
    public void setWeekEndDate(Date weekEndDate) {
        this.weekEndDate = weekEndDate;
    }

    @PropertyName("diary_leaf_colors")
    public String getDiaryLeafColors() {
        return diaryLeafColors;
    }

    @PropertyName("diary_leaf_colors")
    public void setDiaryLeafColors(String diaryLeafColors) {
        this.diaryLeafColors = diaryLeafColors;
    }

    @PropertyName("tree_snapshot")
    public String getTreeSnapshot() {
        return treeSnapshot;
    }

    @PropertyName("tree_snapshot")
    public void setTreeSnapshot(String treeSnapshot) {
        this.treeSnapshot = treeSnapshot;
    }

    @PropertyName("status")
    public String getStatus() {
        return status;
    }

    @PropertyName("status")
    public void setStatus(String status) {
        this.status = status;
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
        return "Tree{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", weekShortDate=" + weekShortDate +
                ", weekEndDate=" + weekEndDate +
                ", diaryLeafColors='" + diaryLeafColors + '\'' +
                ", treeSnapshot='" + treeSnapshot + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}

