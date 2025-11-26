package com.GDG.worktree.team2.gardening_diary.service;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.GDG.worktree.team2.gardening_diary.entity.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.util.List;
import java.util.ArrayList;

@Service
public class UserService {

    private final Firestore db = FirestoreClient.getFirestore();

    //CREATE
    public String createUser(User user) throws ExecutionException, InterruptedException {
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        ApiFuture<WriteResult> future = db.collection("users").document(user.getId()).set(user);
        return future.get().getUpdateTime().toString();
    }

    //READ
    public User getUser(String id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = db.collection("users").document(id);
        DocumentSnapshot document = docRef.get().get();
        return document.exists() ? document.toObject(User.class) : null;
    }

    //UPDATE
    public String updateUser(User user) throws ExecutionException, InterruptedException {
        if (user.getId() == null || user.getId().isEmpty()) {
            throw new IllegalArgumentException("User ID must not be null or empty");
        }
        ApiFuture<WriteResult> future = db.collection("users").document(user.getId()).set(user);
        return future.get().getUpdateTime().toString();
    }

    //DELETE
    public String deleteUser(String id) {
    ApiFuture<WriteResult> future = db.collection("users").document(id).delete();
    try {
        return "Deleted at: " + future.get().getUpdateTime();
    } catch (Exception e) {
        throw new RuntimeException("Failed to delete user", e);
    }
    } 

    // 전체 사용자 조회
    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = db.collection("users").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
    
        List<User> users = new ArrayList<>();
        for (QueryDocumentSnapshot document : documents) {
            users.add(document.toObject(User.class));
        }
        return users;
    }
}
