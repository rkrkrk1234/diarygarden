package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.DiaryRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.GDG.worktree.team2.gardening_diary.service.DiaryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 다이어리 컨트롤러
 */
@RestController
@RequestMapping("/api/diaries")
@CrossOrigin(origins = "*")
public class DiaryController {
    
    private final DiaryService diaryService;

    @Autowired
    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }
    
    /**
     * 다이어리 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Diary>> createDiary(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody DiaryRequest request) {
        
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
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
    public ResponseEntity<ApiResponse<Diary>> getDiary(
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
            }

            Diary diary = diaryService.getDiaryById(id);
            if (diary == null) {
                return ResponseEntity.status(404).body(new ApiResponse<>("다이어리를 찾을 수 없습니다"));
            }

            // 소유권 확인
            if (!userId.equals(diary.getUserId())) {
                return ResponseEntity.status(403).body(new ApiResponse<>("다이어리 조회 권한이 없습니다"));
            }

            return ResponseEntity.ok(new ApiResponse<>(diary, "다이어리 조회 성공"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 모든 다이어리 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Diary>>> getUserDiaries(
            @AuthenticationPrincipal String userId,
            @RequestParam(defaultValue = "0") int limit,
            @RequestParam(required = false) String lastDocId) {
        
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
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
            @AuthenticationPrincipal String userId,
            @PathVariable String id,
            @Valid @RequestBody DiaryRequest request) {
        
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Diary diary = diaryService.updateDiary(id, userId, request);
            return ResponseEntity.ok(new ApiResponse<>(diary, "다이어리가 수정되었습니다"));
            
        } catch (IllegalArgumentException e) {
            // 소유권 관련 예외는 403, 그 외는 400
            if (e.getMessage() != null && e.getMessage().contains("권한")) {
                return ResponseEntity.status(403).body(new ApiResponse<>(e.getMessage()));
            }
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
            @AuthenticationPrincipal String userId,
            @PathVariable String id) {
        
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            boolean success = diaryService.deleteDiary(id, userId);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("다이어리가 삭제되었습니다", "다이어리 삭제 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 삭제 실패"));
            }
            
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("권한")) {
                return ResponseEntity.status(403).body(new ApiResponse<>(e.getMessage()));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 삭제 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 다이어리 개수 조회
     */
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getDiaryCount(@AuthenticationPrincipal String userId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            long count = diaryService.getDiaryCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(count, "다이어리 개수 조회 성공"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 개수 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 나무별 다이어리 조회 엔드포인트
     */
    @GetMapping("/tree/{treeId}")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiariesByTreeId(
            @AuthenticationPrincipal String userId,
            @PathVariable String treeId) {
        try {
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
            }
            List<Diary> diaries = diaryService.getDiariesByTreeId(treeId, userId);
            return ResponseEntity.ok(new ApiResponse<>(diaries, "나무별 다이어리 조회 성공"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("다이어리 조회 실패: " + e.getMessage()));
        }
    }
}

