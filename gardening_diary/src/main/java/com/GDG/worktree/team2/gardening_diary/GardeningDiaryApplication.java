package com.GDG.worktree.team2.gardening_diary;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class GardeningDiaryApplication {

    @Value("${firebase.service-account-file}")
    private String serviceAccountFile;
    
    @Value("${gcp.project-id:diarygarden-7bb2d}")
    private String gcpProjectId;

    public static void main(String[] args) {
        SpringApplication.run(GardeningDiaryApplication.class, args);
    }

    @PostConstruct
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount;
        
        // 1. GCP Secret Manager에서 Firebase 자격 증명 확인
        String firebaseSecret = System.getenv("FIREBASE_SECRET_NAME");
        if (firebaseSecret != null && !firebaseSecret.isEmpty()) {
            try {
                // GCP Secret Manager에서 시크릿 가져오기
                serviceAccount = new java.io.ByteArrayInputStream(getSecretFromGCP(firebaseSecret).getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("Firebase credentials loaded from GCP Secret Manager: " + firebaseSecret);
            } catch (Exception e) {
                System.err.println("Failed to load from Secret Manager: " + e.getMessage());
                throw new RuntimeException("Firebase initialization failed", e);
            }
        }
        // 2. 환경 변수에서 Firebase 자격 증명 확인
        else {
            String firebaseCredentials = System.getenv("FIREBASE_CREDENTIALS");
            if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
                // 환경 변수에서 JSON을 읽어옴 (GCP 배포 시)
                serviceAccount = new java.io.ByteArrayInputStream(firebaseCredentials.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                System.out.println("Firebase credentials loaded from environment variable.");
            } else {
                // 로컬 개발 환경에서는 파일에서 읽어옴
                serviceAccount = new ClassPathResource("diarygarden-7bb2d-firebase-adminsdk-fbsvc-8febd6bf01.json").getInputStream();
                System.out.println("Firebase credentials loaded from classpath.");
            }
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        try {
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase initialized successfully.");
            } else {
                System.out.println("Firebase already initialized.");
            }
        } catch (Exception e) {
            System.err.println("Firebase initialization failed: " + e.getMessage());
            // 테스트 환경에서는 오류를 무시
            if (System.getProperty("spring.profiles.active") == null || 
                !System.getProperty("spring.profiles.active").contains("test")) {
                throw e;
            }
        }
    }
    
    /**
     * GCP Secret Manager에서 시크릿 값 가져오기
     */
    private String getSecretFromGCP(String secretName) {
        try {
            // GCP Secret Manager 클라이언트 사용
            com.google.cloud.secretmanager.v1.SecretManagerServiceClient client = 
                com.google.cloud.secretmanager.v1.SecretManagerServiceClient.create();
            
            com.google.cloud.secretmanager.v1.SecretVersionName secretVersionName = 
                com.google.cloud.secretmanager.v1.SecretVersionName.of(gcpProjectId, secretName, "latest");
            
            com.google.cloud.secretmanager.v1.AccessSecretVersionResponse response = 
                client.accessSecretVersion(secretVersionName);
            
            return response.getPayload().getData().toStringUtf8();
        } catch (Exception e) {
            throw new RuntimeException("Failed to access secret: " + secretName, e);
        }
    }
    
}
