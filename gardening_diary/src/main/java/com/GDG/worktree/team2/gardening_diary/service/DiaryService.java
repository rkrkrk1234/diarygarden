package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.dto.DiaryRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.GDG.worktree.team2.gardening_diary.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 다이어리 서비스
 */
@Service
public class DiaryService {
    
    @Autowired
    private DiaryRepository diaryRepository;
    
    /**
     * 다이어리 생성
     */
    public Diary createDiary(String userId, DiaryRequest request) throws ExecutionException, InterruptedException {
        Diary diary = new Diary();
        diary.setUserId(userId);
        diary.setTreeId(request.getTreeId());
        diary.setContent(request.getContent());
        
        return diaryRepository.save(diary);
    }
    
    /**
     * 다이어리 조회 (ID로)
     */
    public Diary getDiaryById(String id) throws ExecutionException, InterruptedException {
        return diaryRepository.findById(id);
    }
    
    /**
     * 사용자의 모든 다이어리 조회
     */
    public List<Diary> getUserDiaries(String userId) throws ExecutionException, InterruptedException {
        return diaryRepository.findByUserId(userId);
    }
    
    /**
     * 사용자의 다이어리 조회 (페이징)
     */
    public List<Diary> getUserDiariesWithPaging(String userId, int limit, String lastDocId) 
            throws ExecutionException, InterruptedException {
        return diaryRepository.findByUserIdWithPaging(userId, limit, lastDocId);
    }
    
    /**
     * 다이어리 수정
     */
    public Diary updateDiary(String diaryId, String userId, DiaryRequest request) 
            throws ExecutionException, InterruptedException {
        
        // 다이어리 존재 및 소유권 확인
        Diary existingDiary = diaryRepository.findById(diaryId);
        if (existingDiary == null) {
            throw new IllegalArgumentException("다이어리를 찾을 수 없습니다");
        }
        
        if (!userId.equals(existingDiary.getUserId())) {
            throw new IllegalArgumentException("다이어리 수정 권한이 없습니다");
        }
        
        // 필드 업데이트
        if (request.getTreeId() != null) {
            existingDiary.setTreeId(request.getTreeId());
        }
        if (request.getContent() != null) {
            existingDiary.setContent(request.getContent());
        }
        
        return diaryRepository.save(existingDiary);
    }
    
    /**
     * 다이어리 삭제
     */
    public boolean deleteDiary(String diaryId, String userId) 
            throws ExecutionException, InterruptedException {
        
        // 다이어리 존재 및 소유권 확인
        Diary diary = diaryRepository.findById(diaryId);
        if (diary == null) {
            throw new IllegalArgumentException("다이어리를 찾을 수 없습니다");
        }
        
        if (!userId.equals(diary.getUserId())) {
            throw new IllegalArgumentException("다이어리 삭제 권한이 없습니다");
        }
        
        diaryRepository.deleteById(diaryId);
        return true;
    }
    
    /**
     * 다이어리 개수 조회
     */
    public long getDiaryCount(String userId) throws ExecutionException, InterruptedException {
        return diaryRepository.countByUserId(userId);
    }
    
    /**
     * 다이어리 소유권 확인
     */
    public boolean isOwner(String diaryId, String userId) 
            throws ExecutionException, InterruptedException {
        return diaryRepository.isOwner(diaryId, userId);
    }
}

