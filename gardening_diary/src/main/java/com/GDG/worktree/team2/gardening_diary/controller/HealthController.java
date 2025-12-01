package com.GDG.worktree.team2.gardening_diary.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 헬스체크 컨트롤러
 */
@Tag(name = "헬스체크", description = "서비스 상태 확인 API")
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HealthController {
    
    /**
     * 헬스체크 엔드포인트
     */
    @Operation(
        summary = "헬스체크",
        description = "서비스의 상태를 확인합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "서비스 정상 작동 중")
    })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Gardening Diary API is running");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API 정보
     */
    @Operation(
        summary = "API 정보",
        description = "API의 기본 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "API 정보 조회 성공")
    })
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "Gardening Diary API");
        response.put("version", "1.0.0");
        response.put("description", "식물 다이어리 관리 API");
        response.put("endpoints", Map.of(
            "auth", "/api/auth/**",
            "diaries", "/api/diaries/**",
            "health", "/api/health"
        ));
        
        return ResponseEntity.ok(response);
    }
}
