package com.GDG.worktree.team2.gardening_diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Date;

/**
 * 나무 요청 DTO
 */
@Schema(description = "나무 생성/수정 요청")
public class TreeRequest {
    
    @Schema(description = "주 시작 날짜", example = "2024-01-01T00:00:00Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date weekShortDate;

    @Schema(description = "주 종료 날짜", example = "2024-01-07T23:59:59Z")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date weekEndDate;

    @Schema(description = "다이어리 잎 색상", example = "green,yellow,red")
    private String diaryLeafColors;
    
    @Schema(description = "나무 스냅샷", example = "base64_encoded_image")
    private String treeSnapshot;
    
    @Schema(description = "나무 상태", example = "active", allowableValues = {"active", "inactive", "completed"})
    private String status;
    
    // 기본 생성자
    public TreeRequest() {}
    
    // 생성자
    public TreeRequest(Date weekShortDate, Date weekEndDate, String diaryLeafColors, String treeSnapshot, String status) {
        this.weekShortDate = weekShortDate;
        this.weekEndDate = weekEndDate;
        this.diaryLeafColors = diaryLeafColors;
        this.treeSnapshot = treeSnapshot;
        this.status = status;
    }
    
    // Getters and Setters
    public Date getWeekShortDate() {
        return weekShortDate;
    }
    
    public void setWeekShortDate(Date weekShortDate) {
        this.weekShortDate = weekShortDate;
    }
    
    public Date getWeekEndDate() {
        return weekEndDate;
    }
    
    public void setWeekEndDate(Date weekEndDate) {
        this.weekEndDate = weekEndDate;
    }
    
    public String getDiaryLeafColors() {
        return diaryLeafColors;
    }
    
    public void setDiaryLeafColors(String diaryLeafColors) {
        this.diaryLeafColors = diaryLeafColors;
    }
    
    public String getTreeSnapshot() {
        return treeSnapshot;
    }
    
    public void setTreeSnapshot(String treeSnapshot) {
        this.treeSnapshot = treeSnapshot;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "TreeRequest{" +
                "weekShortDate=" + weekShortDate +
                ", weekEndDate=" + weekEndDate +
                ", diaryLeafColors='" + diaryLeafColors + '\'' +
                ", treeSnapshot='" + treeSnapshot + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}