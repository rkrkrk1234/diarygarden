package com.GDG.worktree.team2.gardening_diary.controller;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.dto.RegisterRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 컨트롤러
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 아이디/비밀번호 회원가입
     */
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(response, "회원가입이 완료되었습니다"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(response.getMessage()));
        }
    }
    
    /**
     * 아이디/비밀번호 로그인 (아이디 확인)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(response, "아이디 확인 완료"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(response.getMessage()));
        }
    }
    
    /**
     * 토큰 검증
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyToken(@RequestHeader("Authorization") String token) {
        // Bearer 토큰에서 실제 토큰 추출
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        
        AuthResponse response = authService.verifyToken(token);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(response, "토큰 검증 성공"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(response.getMessage()));
        }
    }
    
    /**
     * 사용자 정보 조회
     */
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<User>> getUserInfo(@AuthenticationPrincipal String uid) {
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
        }
        
        User user = authService.getUserByUid(uid);
        
        if (user != null) {
            return ResponseEntity.ok(new ApiResponse<>(user, "사용자 정보 조회 성공"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("사용자 정보를 찾을 수 없습니다"));
        }
    }
    
    /**
     * 사용자 정보 수정
     */
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @AuthenticationPrincipal String uid,
            @RequestParam(required = false) String displayName,
            @RequestParam(required = false) String profileImageUrl) {
        
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
        }
        
        User user = authService.updateUser(uid, displayName, profileImageUrl);
        
        if (user != null) {
            return ResponseEntity.ok(new ApiResponse<>(user, "사용자 정보 수정 성공"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("사용자 정보 수정 실패"));
        }
    }
    
    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/user")
    public ResponseEntity<ApiResponse<String>> deleteUser(@AuthenticationPrincipal String uid) {
        if (uid == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>("인증이 필요합니다"));
        }
        
        boolean success = authService.deleteUser(uid);
        
        if (success) {
            return ResponseEntity.ok(new ApiResponse<>("회원 탈퇴가 완료되었습니다", "회원 탈퇴 성공"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>("회원 탈퇴 실패"));
        }
    }
}

