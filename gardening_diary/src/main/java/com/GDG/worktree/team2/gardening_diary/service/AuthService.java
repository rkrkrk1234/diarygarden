package com.GDG.worktree.team2.gardening_diary.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.dto.RegisterRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.repository.UserRepository;
import com.GDG.worktree.team2.gardening_diary.security.JwtProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;

/**
 * 인증 서비스
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final FirebaseAuth firebaseAuth;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    
    @Value("${firebase.web-api-key:}")
    private String firebaseWebApiKey;
    
    @Value("${gcp.project-id:diarygarden-7bb2d}")
    private String projectId;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtProvider jwtProvider, FirebaseAuth firebaseAuth) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
        this.firebaseAuth = firebaseAuth; // Spring Bean으로 주입
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newHttpClient();
    }
    
    /**
     * 환경변수에서 Firebase Web API Key 로드
     */
    @PostConstruct
    public void init() {
        // 우선순위: 시스템 프로퍼티 > 환경변수 > application.yml
        String apiKey = System.getProperty("FIREBASE_WEB_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("FIREBASE_WEB_API_KEY");
        }
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = this.firebaseWebApiKey; // application.yml에서 읽은 값
        }
        
        // 따옴표 제거 (있는 경우)
        if (apiKey != null && !apiKey.isEmpty()) {
            apiKey = apiKey.replaceAll("^\"|\"$", "").replaceAll("^'|'$", "").trim();
            this.firebaseWebApiKey = apiKey;
        }
        
        System.out.println("Firebase Web API Key loaded: " + (this.firebaseWebApiKey != null && !this.firebaseWebApiKey.isEmpty() ? "Yes (length: " + this.firebaseWebApiKey.length() + ")" : "No"));
    }
    
    /**
     * Custom Token을 Firebase ID Token으로 변환
     */
    private String exchangeCustomTokenForIdToken(String customToken) throws IOException, InterruptedException {
        if (firebaseWebApiKey == null || firebaseWebApiKey.isEmpty()) {
            throw new IllegalStateException("Firebase Web API Key가 설정되지 않았습니다. .env 파일에 FIREBASE_WEB_API_KEY를 설정해주세요.");
        }
        
        System.out.println("Custom Token을 ID Token으로 변환 시도 중...");
        
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=" + firebaseWebApiKey;
        
        Map<String, String> requestBody = Map.of(
            "token", customToken,
            "returnSecureToken", "true"
        );
        
        String jsonBody = objectMapper.writeValueAsString(requestBody);
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        
        System.out.println("Firebase REST API 응답 상태 코드: " + response.statusCode());
        
        if (response.statusCode() == 200) {
            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            String idToken = (String) responseBody.get("idToken");
            if (idToken != null && !idToken.isEmpty()) {
                System.out.println("Firebase ID Token 획득 성공!");
                return idToken;
            } else {
                System.err.println("응답에 idToken이 없습니다: " + response.body());
                throw new IOException("Firebase ID Token을 받아올 수 없습니다. 응답: " + response.body());
            }
        } else {
            System.err.println("Firebase ID Token 변환 실패: " + response.body());
            throw new IOException("Failed to exchange custom token: HTTP " + response.statusCode() + " - " + response.body());
        }
    }
    
    /**
     * 아이디/비밀번호 회원가입
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            User existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser != null) {
                // 이미 존재하는 사용자면 자동 로그인 처리 후 ID Token 반환
                try {
                    String customToken = firebaseAuth.createCustomToken(existingUser.getUid());
                    String idToken = exchangeCustomTokenForIdToken(customToken);
                    return new AuthResponse(idToken, existingUser.getUid(),
                            request.getUsername(), existingUser.getDisplayName());
                } catch (FirebaseAuthException | IOException | InterruptedException e) {
                    return new AuthResponse("로그인 실패: " + e.getMessage());
                }
            }
            
            String firebaseEmail = request.getUsername() + "@gardening-diary.app";
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(firebaseEmail)
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getDisplayName());
            
            UserRecord userRecord = firebaseAuth.createUser(createRequest);
            
            User user = new User();
            user.setUid(userRecord.getUid());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setDisplayName(userRecord.getDisplayName());
            user.setNickname(userRecord.getDisplayName());
            user.setAuthProvider("USERNAME");
            
            userRepository.save(user);
            
            // Custom Token 생성 후 Firebase ID Token으로 변환
            try {
                String customToken = firebaseAuth.createCustomToken(userRecord.getUid());
                String idToken = exchangeCustomTokenForIdToken(customToken);
                return new AuthResponse(idToken, userRecord.getUid(),
                        request.getUsername(), userRecord.getDisplayName());
            } catch (FirebaseAuthException | IOException | InterruptedException tokenEx) {
                return new AuthResponse("ID Token 생성 실패: " + tokenEx.getMessage());
            }
            
        } catch (FirebaseAuthException e) {
            // Firebase에 이미 존재하지만 Firestore에는 없는 경우 처리
            if (e.getErrorCode() != null && e.getErrorCode().equals("auth/email-already-exists")) {
                User existingUser = userRepository.findByUsername(request.getUsername());
                if (existingUser != null) {
                    try {
                        String customToken = firebaseAuth.createCustomToken(existingUser.getUid());
                        String idToken = exchangeCustomTokenForIdToken(customToken);
                        return new AuthResponse(idToken, existingUser.getUid(),
                                request.getUsername(), existingUser.getDisplayName());
                    } catch (FirebaseAuthException | IOException | InterruptedException tokenEx) {
                        return new AuthResponse("로그인 실패: " + tokenEx.getMessage());
                    }
                }
            }
            return new AuthResponse("회원가입 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) return new AuthResponse("존재하지 않는 아이디입니다");
            
            if (user.getPassword() == null) return new AuthResponse("비밀번호 로그인을 지원하지 않습니다");
            if (request.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse("비밀번호가 일치하지 않습니다");
            }
            
            // Custom Token 생성 후 Firebase ID Token으로 변환
            try {
                String customToken = firebaseAuth.createCustomToken(user.getUid());
                System.out.println("Custom Token 생성 완료");
                
                String idToken = exchangeCustomTokenForIdToken(customToken);
                return new AuthResponse(idToken, user.getUid(), request.getUsername(), user.getDisplayName());
            } catch (FirebaseAuthException | IOException | InterruptedException e) {
                return new AuthResponse("ID Token 생성 실패: " + e.getMessage());
            }
            
        } catch (Exception e) {
            return new AuthResponse("로그인 실패: " + e.getMessage());
        }
    }
    
    public AuthResponse verifyToken(String idToken) {
        try {
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            
            User user = userRepository.findByUid(uid);
            if (user == null) return new AuthResponse("사용자 정보를 찾을 수 없습니다");
            
            String identifier = user.getEmail() != null ? user.getEmail() : user.getUsername();
            return new AuthResponse(idToken, uid, identifier, user.getDisplayName());
            
        } catch (FirebaseAuthException e) {
            return new AuthResponse("토큰 검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    public User getUserByUid(String uid) {
        try {
            return userRepository.findByUid(uid);
        } catch (Exception e) {
            System.err.println("사용자 조회 실패: " + e.getMessage());
            return null;
        }
    }
    
    public User updateUser(String uid, String displayName, String profileImageUrl) {
        try {
            User user = userRepository.findByUid(uid);
            if (user == null) return null;
            
            if (displayName != null) user.setDisplayName(displayName);
            if (profileImageUrl != null) user.setProfileImageUrl(profileImageUrl);
            
            return userRepository.save(user);
            
        } catch (Exception e) {
            System.err.println("사용자 업데이트 실패: " + e.getMessage());
            return null;
        }
    }
    
    public boolean deleteUser(String uid) {
        try {
            firebaseAuth.deleteUser(uid);
            userRepository.deleteByUid(uid);
            return true;
        } catch (Exception e) {
            System.err.println("사용자 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    public AuthResponse generateTokenForSocialUser(User user) {
        try {
            // Custom Token 생성 후 Firebase ID Token으로 변환
            String customToken = firebaseAuth.createCustomToken(user.getUid());
            String idToken = exchangeCustomTokenForIdToken(customToken);
            
            AuthResponse response = new AuthResponse();
            response.setSuccess(true);
            response.setMessage("소셜 로그인 성공");
            response.setToken(idToken);
            response.setUid(user.getUid());
            return response;
        } catch (Exception e) {
            AuthResponse response = new AuthResponse();
            response.setSuccess(false);
            response.setMessage("토큰 생성 실패: " + e.getMessage());
            return response;
        }
    }
}
