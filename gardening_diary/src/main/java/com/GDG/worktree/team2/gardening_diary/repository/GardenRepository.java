package com.GDG.worktree.team2.gardening_diary.repository;

import com.GDG.worktree.team2.gardening_diary.entity.Garden;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 정원 Repository
 */
@Repository
public class GardenRepository {
    
    private static final String COLLECTION_NAME = "gardens";
    
    @Autowired
    private Firestore firestore;
    
    /**
     * 정원 저장
     */
    public Garden save(Garden garden) throws ExecutionException, InterruptedException {
        // ID가 없으면 자동 생성
        if (garden.getId() == null || garden.getId().isEmpty()) {
            garden.setId(java.util.UUID.randomUUID().toString());
        }
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(garden.getId())
                .set(garden);
        
        future.get();
        return garden;
    }
    
    /**
     * 정원 조회 (ID로)
     */
    public Garden findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Garden.class);
        }
        return null;
    }
    
    /**
     * 사용자의 정원 조회
     */
    public List<Garden> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Garden> gardens = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            gardens.add(document.toObject(Garden.class));
        }
        return gardens;
    }
    
    /**
     * 정원 삭제
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete();
        
        future.get();
    }
}






