package com.GDG.worktree.team2.gardening_diary.repository;

import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 사용자 Repository
 */
@Repository
public class UserRepository {
    
    private static final String COLLECTION_NAME = "users";
    
    @Autowired
    private Firestore firestore;
    
    /**
     * 사용자 저장
     */
    public User save(User user) throws ExecutionException, InterruptedException {
        // ID가 없으면 자동 생성
        if (user.getId() == null || user.getId().isEmpty()) {
            user.setId(java.util.UUID.randomUUID().toString());
        }
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(user.getId())
                .set(user);
        
        future.get(); // 완료 대기
        return user;
    }
    
    /**
     * 사용자 조회 (ID로)
     */
    public User findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(User.class);
        }
        return null;
    }
    
    /**
     * 이메일로 사용자 조회
     */
    public User findByEmail(String email) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .limit(1);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        if (!documents.isEmpty()) {
            DocumentSnapshot document = documents.getDocuments().get(0);
            return document.toObject(User.class);
        }
        return null;
    }
    
    /**
     * 아이디로 사용자 조회
     */
    public User findByUsername(String username) {
        try {
            Query query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("username", username)
                    .limit(1);
            
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot documents = querySnapshot.get();
            
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.getDocuments().get(0);
                return document.toObject(User.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("아이디로 사용자 조회 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * UID로 사용자 조회
     */
    public User findByUid(String uid) {
        try {
            Query query = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("uid", uid)
                    .limit(1);
            
            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot documents = querySnapshot.get();
            
            if (!documents.isEmpty()) {
                DocumentSnapshot document = documents.getDocuments().get(0);
                return document.toObject(User.class);
            }
            return null;
        } catch (Exception e) {
            System.err.println("UID로 사용자 조회 실패: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * 사용자 삭제
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete();
        
        future.get(); // 완료 대기
    }
    
    /**
     * UID로 사용자 삭제
     */
    public void deleteByUid(String uid) {
        try {
            User user = findByUid(uid);
            if (user != null && user.getId() != null) {
                deleteById(user.getId());
            }
        } catch (Exception e) {
            System.err.println("UID로 사용자 삭제 실패: " + e.getMessage());
        }
    }
    
    /**
     * 모든 사용자 조회
     */
    public List<User> findAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
        QuerySnapshot querySnapshot = future.get();
        
        List<User> users = new ArrayList<>();
        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
            users.add(document.toObject(User.class));
        }
        return users;
    }
    
    /**
     * 사용자 존재 여부 확인
     */
    public boolean existsById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        return document.exists();
    }
}
