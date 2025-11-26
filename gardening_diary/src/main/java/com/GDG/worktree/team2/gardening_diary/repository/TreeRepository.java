package com.GDG.worktree.team2.gardening_diary.repository;

import com.GDG.worktree.team2.gardening_diary.entity.Tree;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 나무 Repository
 */
@Repository
public class TreeRepository {
    
    private static final String COLLECTION_NAME = "trees";
    
    @Autowired
    private Firestore firestore;
    
    /**
     * 나무 저장
     */
    public Tree save(Tree tree) throws ExecutionException, InterruptedException {
        // ID가 없으면 자동 생성
        if (tree.getId() == null || tree.getId().isEmpty()) {
            tree.setId(java.util.UUID.randomUUID().toString());
        }
        
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(tree.getId())
                .set(tree);
        
        future.get();
        return tree;
    }
    
    /**
     * 나무 조회 (ID로)
     */
    public Tree findById(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.toObject(Tree.class);
        }
        return null;
    }
    
    /**
     * 사용자의 모든 나무 조회
     */
    public List<Tree> findByUserId(String userId) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("user_id", userId);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Tree> trees = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            trees.add(document.toObject(Tree.class));
        }
        return trees;
    }
    
    /**
     * 사용자 ID와 상태로 나무 조회
     */
    public List<Tree> findByUserIdAndStatus(String userId, String status) throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("user_id", userId)
                .whereEqualTo("status", status);
        
        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        QuerySnapshot documents = querySnapshot.get();
        
        List<Tree> trees = new ArrayList<>();
        for (DocumentSnapshot document : documents.getDocuments()) {
            trees.add(document.toObject(Tree.class));
        }
        return trees;
    }
    
    /**
     * 나무 삭제
     */
    public void deleteById(String id) throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = firestore.collection(COLLECTION_NAME)
                .document(id)
                .delete();
        
        future.get();
    }
}

