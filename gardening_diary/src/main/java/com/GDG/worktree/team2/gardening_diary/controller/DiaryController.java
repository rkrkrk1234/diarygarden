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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 다이어리 컨트롤러
 */
@Tag(name = "다이어리 관리", description = "다이어리 생성, 조회, 수정, 삭제 API")
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
    @Operation(
        summary = "다이어리 생성",
        description = "새로운 다이어리를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 생성 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Diary>> createDiary(
            @AuthenticationPrincipal String userId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "다이어리 생성 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = DiaryRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DiaryRequest request) {
        
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
    @Operation(
        summary = "다이어리 조회 (ID로)",
        description = "다이어리 ID로 다이어리 정보를 조회합니다. 본인의 다이어리만 조회 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리를 찾을 수 없거나 조회 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "조회 권한 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> getDiary(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "다이어리 ID", required = true, example = "diary123")
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
    @Operation(
        summary = "사용자의 다이어리 목록 조회",
        description = "현재 인증된 사용자의 모든 다이어리를 조회합니다. 페이징 지원 (limit > 0일 경우)."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 목록 조회 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Diary>>> getUserDiaries(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "조회할 개수 (0이면 전체 조회)", example = "10")
            @RequestParam(defaultValue = "0") int limit,
            @Parameter(description = "페이징용 마지막 문서 ID", example = "lastDocId123")
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
    @Operation(
        summary = "다이어리 수정",
        description = "다이어리 정보를 수정합니다. 본인의 다이어리만 수정 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 수정 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "수정 권한 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Diary>> updateDiary(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "다이어리 ID", required = true, example = "diary123")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "다이어리 수정 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = DiaryRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody DiaryRequest request) {
        
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
    @Operation(
        summary = "다이어리 삭제",
        description = "다이어리를 삭제합니다. 본인의 다이어리만 삭제 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 삭제 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "삭제 권한 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDiary(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "다이어리 ID", required = true, example = "diary123")
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
    @Operation(
        summary = "다이어리 개수 조회",
        description = "현재 인증된 사용자의 다이어리 개수를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "다이어리 개수 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 개수 조회 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(
        summary = "나무별 다이어리 조회",
        description = "특정 나무에 속한 다이어리 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무별 다이어리 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "다이어리 조회 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @GetMapping("/tree/{treeId}")
    public ResponseEntity<ApiResponse<List<Diary>>> getDiariesByTreeId(
            @AuthenticationPrincipal String userId,
            @Parameter(description = "나무 ID", required = true, example = "tree123")
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

