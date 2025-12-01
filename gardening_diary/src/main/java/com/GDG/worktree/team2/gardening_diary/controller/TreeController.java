package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.TreeRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Tree;
import com.GDG.worktree.team2.gardening_diary.service.TreeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
 * 나무 컨트롤러
 */
@Tag(name = "나무 관리", description = "나무 생성, 조회, 수정, 삭제 API")
@RestController
@RequestMapping("/api/trees")
@CrossOrigin(origins = "*")
public class TreeController {
    
    @Autowired
    private TreeService treeService;
    
    /**
     * 나무 생성
     */
    @Operation(
        summary = "나무 생성",
        description = "새로운 나무를 생성합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "나무 생성 실패 또는 인증 실패")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Tree>> createTree(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "나무 생성 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = TreeRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody TreeRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Tree tree = treeService.createTree(userId, request);
            return ResponseEntity.ok(new ApiResponse<>(tree, "나무가 생성되었습니다"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("나무 생성 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 나무 조회 (ID로)
     */
    @Operation(
        summary = "나무 조회 (ID로)",
        description = "나무 ID로 나무 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "나무를 찾을 수 없거나 조회 실패")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tree>> getTree(
            @Parameter(description = "나무 ID", required = true, example = "tree123")
            @PathVariable String id) {
        try {
            Tree tree = treeService.getTreeById(id);
            
            if (tree != null) {
                return ResponseEntity.ok(new ApiResponse<>(tree, "나무 조회 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("나무를 찾을 수 없습니다"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("나무 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 모든 나무 조회
     */
    @Operation(
        summary = "사용자의 나무 목록 조회",
        description = "현재 인증된 사용자의 모든 나무를 조회합니다. 상태별 필터링 지원."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "나무 목록 조회 실패 또는 인증 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<List<Tree>>> getUserTrees(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "나무 상태 필터 (선택사항: active, inactive, completed)", example = "active")
            @RequestParam(required = false) String status) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            List<Tree> trees;
            if (status != null && !status.isEmpty()) {
                trees = treeService.getUserTreesByStatus(userId, status);
            } else {
                trees = treeService.getUserTrees(userId);
            }
            
            return ResponseEntity.ok(new ApiResponse<>(trees, "나무 목록 조회 성공"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("나무 목록 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 나무 수정
     */
    @Operation(
        summary = "나무 수정",
        description = "나무 정보를 수정합니다. 나무 소유자만 수정 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "나무 수정 실패, 권한 없음 또는 인증 실패")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tree>> updateTree(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "나무 ID", required = true, example = "tree123")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "나무 수정 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = TreeRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody TreeRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Tree tree = treeService.updateTree(id, userId, request);
            return ResponseEntity.ok(new ApiResponse<>(tree, "나무가 수정되었습니다"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("나무 수정 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 나무 삭제
     */
    @Operation(
        summary = "나무 삭제",
        description = "나무를 삭제합니다. 나무 소유자만 삭제 가능합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "나무 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "나무 삭제 실패, 권한 없음 또는 인증 실패")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTree(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "나무 ID", required = true, example = "tree123")
            @PathVariable String id) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            boolean success = treeService.deleteTree(id, userId);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("나무가 삭제되었습니다", "나무 삭제 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("나무 삭제 실패"));
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("나무 삭제 실패: " + e.getMessage()));
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



