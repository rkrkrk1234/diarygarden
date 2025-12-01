package com.GDG.worktree.team2.gardening_diary.entity;

import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.ServerTimestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

/**
 * 감정 분석 엔티티
 */
@Schema(description = "감정 분석 결과")
public class EmotionAnalysis {
    @Schema(description = "감정 분석 ID", example = "emotion123")
    @DocumentId
    private String id;
    
    @Schema(description = "다이어리 ID", example = "diary123")
    private String diaryId; // 다이어리 ID
    
    @Schema(description = "감정 분석 결과 (감정명: 점수)", example = "{\"기쁨\": 0.8, \"슬픔\": 0.2}")
    private Map<String, Double> result; // 감정 분석 결과 (예: {"기쁨": 0.8, "슬픔": 0.2})
    
    @Schema(description = "생성 일시", example = "2024-01-01T00:00:00")
    @ServerTimestamp
    private Date createdAt;

    @Schema(description = "수정 일시", example = "2024-01-01T00:00:00")
    @ServerTimestamp
    private Date updatedAt;
    
    // 기본 생성자
    public EmotionAnalysis() {}
    
    // 생성자
    public EmotionAnalysis(String diaryId, Map<String, Double> result) {
        this.diaryId = diaryId;
        this.result = result;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getDiaryId() {
        return diaryId;
    }
    
    public void setDiaryId(String diaryId) {
        this.diaryId = diaryId;
    }
    
    public Map<String, Double> getResult() {
        return result;
    }
    
    public void setResult(Map<String, Double> result) {
        this.result = result;
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
        return "EmotionAnalysis{" +
                "id='" + id + '\'' +
                ", diaryId='" + diaryId + '\'' +
                ", result=" + result +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}


