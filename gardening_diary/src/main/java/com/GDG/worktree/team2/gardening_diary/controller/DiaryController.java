package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.DiaryRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.GDG.worktree.team2.gardening_diary.service.DiaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 다이어리 컨트롤러
 */
@RestController
@RequestMapping("/api/diaries")
@CrossOrigin(origins = "*")
public class DiaryController {
    
    @Autowired
    private DiaryService diaryService;
    
    /**
     * 다이어리 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Diary>> createDiary(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody DiaryRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Diary diary = diaryService.createDiary(userId, request);
            return ResponseEntity.ok(new ApiResponse<>(diary, "다이어리가 생성되었습니다"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 생성 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 다이어리 조회 (ID로)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> getDiary(@PathVariable String id) {
        try {
            Diary diary = diaryService.getDiaryById(id);
            
            if (diary != null) {
                return ResponseEntity.ok(new ApiResponse<>(diary, "다이어리 조회 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리를 찾을 수 없습니다"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 모든 다이어리 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Diary>>> getUserDiaries(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "0") int limit,
            @RequestParam(required = false) String lastDocId) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            List<Diary> diaries;
            if (limit > 0) {
                diaries = diaryService.getUserDiariesWithPaging(userId, limit, lastDocId);
            } else {
                diaries = diaryService.getUserDiaries(userId);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(diaries, "다이어리 목록 조회 성공"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 목록 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 다이어리 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> updateDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @Valid @RequestBody DiaryRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Diary diary = diaryService.updateDiary(id, userId, request);
            return ResponseEntity.ok(new ApiResponse<>(diary, "다이어리가 수정되었습니다"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 수정 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 다이어리 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiary(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            boolean success = diaryService.deleteDiary(id, userId);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("다이어리가 삭제되었습니다", "다이어리 삭제 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 삭제 실패"));
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 삭제 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 다이어리 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getDiaryCount(@RequestHeader("Authorization") String token) {
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            long count = diaryService.getDiaryCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(count, "다이어리 개수 조회 성공"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 개수 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 토큰에서 사용자 ID 추출하는 헬퍼 메서드
     */
    private String getUserIdFromToken(String token) {
        try {
            // Bearer 토큰에서 실제 토큰 추출
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            com.google.firebase.auth.FirebaseToken decodedToken = 
                    com.google.firebase.auth.FirebaseAuth.getInstance().verifyIdToken(token);
            
            return decodedToken.getUid();
            
        } catch (Exception e) {
            System.err.println("토큰 검증 실패: " + e.getMessage());
            return null;
        }
    }
}

