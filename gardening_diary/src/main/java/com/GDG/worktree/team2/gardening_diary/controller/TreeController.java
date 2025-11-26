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

/**
 * 나무 컨트롤러
 */
@RestController
@RequestMapping("/api/trees")
@CrossOrigin(origins = "*")
public class TreeController {
    
    @Autowired
    private TreeService treeService;
    
    /**
     * 나무 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Tree>> createTree(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody TreeRequest request) {
        
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Tree>> getTree(@PathVariable String id) {
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
    @GetMapping
    public ResponseEntity<ApiResponse<List<Tree>>> getUserTrees(
            @RequestHeader("Authorization") String token,
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
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Tree>> updateTree(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @Valid @RequestBody TreeRequest request) {
        
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
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteTree(
            @RequestHeader("Authorization") String token,
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

