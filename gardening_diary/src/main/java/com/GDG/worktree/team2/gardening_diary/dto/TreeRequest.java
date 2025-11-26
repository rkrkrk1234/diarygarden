package com.GDG.worktree.team2.gardening_diary.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;

/**
 * 나무 요청 DTO
 */
public class TreeRequest {
    
    // 2. 날짜 필드 위에 @JsonFormat 어노테이션
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date weekShortDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Date weekEndDate;

    private String diaryLeafColors;
    private String treeSnapshot;
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