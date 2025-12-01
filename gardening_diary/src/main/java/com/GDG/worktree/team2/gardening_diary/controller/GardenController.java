package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.GardenRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Garden;
import com.GDG.worktree.team2.gardening_diary.service.GardenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 정원 컨트롤러
 */
@Tag(name = "정원 관리", description = "정원 생성, 조회, 수정, 삭제 API")
@RestController
@RequestMapping("/api/gardens")
@CrossOrigin(origins = "*")
public class GardenController {
    
    @Autowired
    private GardenService gardenService;
    
    /**
     * 정원 생성 또는 업데이트
     */
    @Operation(
        summary = "정원 생성 또는 업데이트",
        description = "사용자의 정원을 생성하거나 업데이트합니다. 정원이 이미 존재하면 업데이트됩니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정원 생성/업데이트 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패 또는 잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Garden>> createOrUpdateGarden(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "정원 생성/수정 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = GardenRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody GardenRequest request) {
        
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
    @Operation(
        summary = "정원 조회 (ID로)",
        description = "정원 ID를 통해 정원 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정원 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "정원을 찾을 수 없거나 조회 실패")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Garden>> getGarden(
            @Parameter(description = "정원 ID", required = true, example = "garden123")
            @PathVariable String id) {
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
    @Operation(
        summary = "사용자의 정원 조회",
        description = "현재 인증된 사용자의 정원 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정원 조회 성공 (정원이 없을 수도 있음)"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패 또는 조회 실패")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Garden>> getUserGarden(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
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
    @Operation(
        summary = "정원 수정",
        description = "기존 정원의 정보를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정원 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패, 권한 없음 또는 잘못된 요청")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Garden>> updateGarden(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "정원 ID", required = true, example = "garden123")
            @PathVariable String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "정원 수정 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = GardenRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody GardenRequest request) {
        
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
    @Operation(
        summary = "정원 삭제",
        description = "정원을 삭제합니다. 정원 소유자만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "정원 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패, 권한 없음 또는 삭제 실패")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGarden(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token,
            @Parameter(description = "정원 ID", required = true, example = "garden123")
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

