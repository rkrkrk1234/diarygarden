package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.dto.AuthResponse;
import com.GDG.worktree.team2.gardening_diary.dto.LoginRequest;
import com.GDG.worktree.team2.gardening_diary.dto.RegisterRequest;
import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.GDG.worktree.team2.gardening_diary.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 인증 서비스
 */
@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private FirebaseAuth firebaseAuth;
    
    @Autowired
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * FirebaseAuth 인스턴스 가져오기 (지연 초기화)
     */
    private FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
    
    /**
     * 아이디/비밀번호 회원가입
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            // 아이디 중복 확인
            User existingUser = userRepository.findByUsername(request.getUsername());
            if (existingUser != null) {
                return new AuthResponse("이미 존재하는 아이디입니다");
            }
            
            // Firebase Auth용 이메일 형식 변환 (아이디@gardening-diary.app)
            String firebaseEmail = request.getUsername() + "@gardening-diary.app";
            
            // Firebase Auth에 사용자 생성
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(firebaseEmail)
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getDisplayName());
            
            UserRecord userRecord = getFirebaseAuth().createUser(createRequest);
            
            // Firestore에 사용자 정보 저장
            User user = new User();
            user.setUid(userRecord.getUid());
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setDisplayName(userRecord.getDisplayName());
            user.setNickname(request.getDisplayName());
            user.setAuthProvider("USERNAME");
            
            userRepository.save(user);
            
            // 커스텀 토큰 생성
            String customToken = getFirebaseAuth().createCustomToken(userRecord.getUid());
            
            return new AuthResponse(customToken, userRecord.getUid(), 
                    request.getUsername(), userRecord.getDisplayName());
            
        } catch (FirebaseAuthException e) {
            return new AuthResponse("회원가입 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 아이디/비밀번호 로그인
     * 참고: 실제 로그인은 클라이언트에서 Firebase Auth SDK를 사용합니다.
     * 클라이언트는 아이디를 "아이디@gardening-diary.app" 형식으로 변환하여 로그인해야 합니다.
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // 아이디로 사용자 찾기
            User user = userRepository.findByUsername(request.getUsername());
            if (user == null) {
                return new AuthResponse("존재하지 않는 아이디입니다");
            }
            
            if (user.getPassword() == null) {
                return new AuthResponse("해당 계정은 비밀번호 로그인을 지원하지 않습니다");
            }
            if (request.getPassword() == null ||
                    !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse("비밀번호가 일치하지 않습니다");
            }
            return new AuthResponse("", user.getUid(), request.getUsername(), user.getDisplayName());
            
        } catch (Exception e) {
            return new AuthResponse("로그인 실패: " + e.getMessage());
        }
    }
    
    /**
     * ID 토큰 검증
     */
    public AuthResponse verifyToken(String idToken) {
        try {
            FirebaseToken decodedToken = getFirebaseAuth().verifyIdToken(idToken);
            String uid = decodedToken.getUid();
            
            // Firestore에서 사용자 정보 조회
            User user = userRepository.findByUid(uid);
            if (user == null) {
                return new AuthResponse("사용자 정보를 찾을 수 없습니다");
            }
            
            String identifier = user.getEmail() != null ? user.getEmail() : user.getUsername();
            return new AuthResponse(idToken, uid, identifier, user.getDisplayName());
            
        } catch (FirebaseAuthException e) {
            return new AuthResponse("토큰 검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return new AuthResponse("서버 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자 정보 조회
     */
    public User getUserByUid(String uid) {
        try {
            return userRepository.findByUid(uid);
        } catch (Exception e) {
            System.err.println("사용자 조회 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 사용자 정보 업데이트
     */
    public User updateUser(String uid, String displayName, String profileImageUrl) {
        try {
            User user = userRepository.findByUid(uid);
            if (user == null) {
                return null;
            }
            
            if (displayName != null) {
                user.setDisplayName(displayName);
            }
            if (profileImageUrl != null) {
                user.setProfileImageUrl(profileImageUrl);
            }
            
            return userRepository.save(user);
            
        } catch (Exception e) {
            System.err.println("사용자 업데이트 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 사용자 삭제
     */
    public boolean deleteUser(String uid) {
        try {
            // Firebase Auth에서 사용자 삭제
            getFirebaseAuth().deleteUser(uid);
            
            // Firestore에서 사용자 정보 삭제
            userRepository.deleteByUid(uid);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("사용자 삭제 실패: " + e.getMessage());
            return false;
        }
    }
}

