package com.GDG.worktree.team2.gardening_diary.repository;

import com.GDG.worktree.team2.gardening_diary.entity.EmotionAnalysis;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;

/**
 * 감정 분석 Repository
 */
@Repository
public class EmotionAnalysisRepository {
    
    private static final String COLLECTION_NAME = "emotion_analysis";
    
    @Autowired
    private Firestore firestore;
    
    /**
     * 감정 분석 저장
     */
    public EmotionAnalysis save(EmotionAnalysis analysis) throws ExecutionException, InterruptedException {
        // ID가 없으면 자동 생성
        if (analysis.getId() == null || analysis.getId().isEmpty()) {
            analysis.setId(java.util.UUID.randomUUID().toString());
        }
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(analysis.getId())
                .set(analysis);
        
        future.get();
        return analysis;
    }
    
    /**
     * 감정 분석 조회 (ID로)
     */
    public EmotionAnalysis findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(EmotionAnalysis.class);
        }
        return null;
    }
    
    /**
     * 다이어리의 감정 분석 조회
     */
    public EmotionAnalysis findByDiaryId(String diaryId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("diaryId", diaryId)
                .limit(1);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        if (!documents.isEmpty()) {
            DocumentSnapshot document = documents.getDocuments().get(0);
            return document.toObject(EmotionAnalysis.class);
        }
        return null;
    }
    
    /**
     * 감정 분석 삭제
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete();
        
        future.get();
    }
}

