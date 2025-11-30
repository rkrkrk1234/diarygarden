package com.GDG.worktree.team2.gardening_diary.repository;

import com.GDG.worktree.team2.gardening_diary.entity.Diary;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 다이어리 Repository
 */
@Repository
public class DiaryRepository {
    
    private static final String COLLECTION_NAME = "diaries";
    
    @Autowired
    private Firestore firestore;
    
    /**
     * 다이어리 저장
     */
    public Diary save(Diary diary) throws ExecutionException, InterruptedException {
        if (diary.getId() == null) {
            // 새 다이어리인 경우
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document();
            diary.setId(docRef.getId());
        }
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(diary.getId())
                .set(diary);
        
        future.get(); // 완료 대기
        return diary;
    }
    
    /**
     * 다이어리 조회 (ID로)
     */
    public Diary findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Diary.class);
        }
        return null;
    }
    
    /**
     * 사용자의 모든 다이어리 조회
     */
    public List<Diary> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Diary> diaries = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            diaries.add(document.toObject(Diary.class));
        }
        return diaries;
    }
    
    /**
     * 사용자의 다이어리 조회 (페이징)
     */
    public List<Diary> findByUserIdWithPaging(String userId, int limit, String lastDocId) 
            throws ExecutionException, InterruptedException {
        
    Query query = firestore.collection(COLLECTION_NAME)
        .whereEqualTo("userId", userId)
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(limit);
        
        // 페이징을 위한 커서 처리
        if (lastDocId != null && !lastDocId.isEmpty()) {
            DocumentSnapshot lastDoc = firestore.collection(COLLECTION_NAME)
                    .document(lastDocId).get().get();
            if (lastDoc.exists()) {
                query = query.startAfter(lastDoc);
            }
        }
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Diary> diaries = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            diaries.add(document.toObject(Diary.class));
        }
        return diaries;
    }
    
    /**
     * 나무별 다이어리 조회 (사용자 제한)
     */
    public List<Diary> findByTreeId(String treeId, String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("treeId", treeId)
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.ASCENDING);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Diary> diaries = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            diaries.add(document.toObject(Diary.class));
        }
        return diaries;
    }
    
    /**
     * 다이어리 삭제
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete();
        
        future.get(); // 완료 대기
    }
    
    /**
     * 사용자의 다이어리 개수 조회
     */
    public long countByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        return documents.getDocuments().size();
    }
    
    /**
     * 다이어리 존재 여부 확인
     */
    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        return document.exists();
    }
    
    /**
     * 다이어리 소유자 확인
     */
    public boolean isOwner(String diaryId, String userId) throws ExecutionException, InterruptedException {
        Diary diary = findById(diaryId);
        return diary != null && userId.equals(diary.getUserId());
    }
}
