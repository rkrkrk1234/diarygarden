package com.GDG.worktree.team2.gardening_diary.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.GDG.worktree.team2.gardening_diary.dto.ApiResponse;
import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.dto.RegisterRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.service.AuthService;
import com.GDG.worktree.team2.gardening_diary.service.GoogleAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 인증 컨트롤러
 */
@Tag(name = "인증 관리", description = "회원가입, 로그인, 토큰 검증 API")
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;
    private final GoogleAuthService googleAuthService;

    /**
     * 아이디/비밀번호 회원가입
     */
    @Operation(
        summary = "회원가입",
        description = "아이디/비밀번호로 회원가입을 진행합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원가입 실패 (아이디 중복 등)")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "회원가입 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = RegisterRequest.class))
            )
            @org.springframework.web.bind.annotation.RequestBody RegisterRequest request) {
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
    @Operation(
        summary = "로그인",
        description = "아이디/비밀번호로 로그인합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그인 실패 (아이디/비밀번호 불일치)")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "로그인 요청",
                required = true,
                content = @Content(schema = @Schema(implementation = LoginRequest.class))
            )
            @Valid @org.springframework.web.bind.annotation.RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(new ApiResponse<>(response, "아이디 확인 완료"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(response.getMessage()));
        }
    }

    // 구글 로그인
    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse>> googleLogin(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");

        if (idToken == null || idToken.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("Google ID Token missing"));
        }

        try {
            User user = googleAuthService.loginWithGoogle(idToken);
            AuthResponse tokenResponse = authService.generateTokenForSocialUser(user);
            return ResponseEntity.ok(new ApiResponse<>(tokenResponse, "구글 로그인 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>("구글 로그인 실패: " + e.getMessage()));
        }
    }
    
    /**
     * 토큰 검증
     */
    @Operation(
        summary = "토큰 검증",
        description = "Firebase 인증 토큰을 검증합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "토큰 검증 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "토큰 검증 실패")
    })
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<AuthResponse>> verifyToken(
            @Parameter(description = "Firebase 인증 토큰 (Bearer 토큰)", required = true, example = "Bearer eyJhbGciOiJSUzI1NiIs...")
            @RequestHeader("Authorization") String token) {
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
    @Operation(
        summary = "사용자 정보 조회",
        description = "현재 인증된 사용자의 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자 정보를 찾을 수 없음"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
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
    @Operation(
        summary = "사용자 정보 수정",
        description = "현재 인증된 사용자의 표시 이름과 프로필 이미지를 수정합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "사용자 정보 수정 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
    @PutMapping("/user")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @AuthenticationPrincipal String uid,
            @Parameter(description = "표시 이름", example = "홍길동")
            @RequestParam(required = false) String displayName,
            @Parameter(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
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
    @Operation(
        summary = "회원 탈퇴",
        description = "현재 인증된 사용자의 계정을 삭제합니다."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 탈퇴 실패"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 필요")
    })
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

