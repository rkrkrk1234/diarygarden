package com.GDG.worktree.team2.gardening_diary.config;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Firestore 설정
 */
@Configuration
public class FirestoreConfig {
    
    @Value("${gcp.project-id:diarygarden-7bb2d}")
    private String projectId;
    
    @Bean
    public Firestore firestore() {
        // Firestore 인스턴스 생성 (프로젝트 ID 명시적 설정)
        FirestoreOptions options = FirestoreOptions.newBuilder()
                .setProjectId(projectId)
                .build();
        
        return options.getService();
    }
}

