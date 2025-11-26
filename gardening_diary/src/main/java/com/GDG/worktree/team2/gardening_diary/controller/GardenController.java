package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.GardenRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Garden;
import com.GDG.worktree.team2.gardening_diary.service.GardenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 정원 컨트롤러
 */
@RestController
@RequestMapping("/api/gardens")
@CrossOrigin(origins = "*")
public class GardenController {
    
    @Autowired
    private GardenService gardenService;
    
    /**
     * 정원 생성 또는 업데이트
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Garden>> createOrUpdateGarden(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody GardenRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Garden garden = gardenService.createOrUpdateGarden(userId, request);
            return ResponseEntity.ok(new ApiResponse<>(garden, "정원이 생성/업데이트되었습니다"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("정원 생성/업데이트 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 정원 조회 (ID로)
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Garden>> getGarden(@PathVariable String id) {
        try {
            Garden garden = gardenService.getGardenById(id);
            
            if (garden != null) {
                return ResponseEntity.ok(new ApiResponse<>(garden, "정원 조회 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("정원을 찾을 수 없습니다"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("정원 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 사용자의 정원 조회
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Garden>> getUserGarden(
            @RequestHeader("Authorization") String token) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Garden garden = gardenService.getUserGarden(userId);
            
            if (garden != null) {
                return ResponseEntity.ok(new ApiResponse<>(garden, "정원 조회 성공"));
            } else {
                return ResponseEntity.ok(new ApiResponse<>(null, "정원이 없습니다"));
            }
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("정원 조회 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 정원 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Garden>> updateGarden(
            @RequestHeader("Authorization") String token,
            @PathVariable String id,
            @Valid @RequestBody GardenRequest request) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            Garden garden = gardenService.updateGarden(id, userId, request);
            return ResponseEntity.ok(new ApiResponse<>(garden, "정원이 수정되었습니다"));
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("정원 수정 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 정원 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGarden(
            @RequestHeader("Authorization") String token,
            @PathVariable String id) {
        
        try {
            // 토큰 검증 및 사용자 ID 추출
            String userId = getUserIdFromToken(token);
            if (userId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>("인증이 필요합니다"));
            }
            
            boolean success = gardenService.deleteGarden(id, userId);
            
            if (success) {
                return ResponseEntity.ok(new ApiResponse<>("정원이 삭제되었습니다", "정원 삭제 성공"));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>("정원 삭제 실패"));
            }
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("정원 삭제 실패: " + e.getMessage()));
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

