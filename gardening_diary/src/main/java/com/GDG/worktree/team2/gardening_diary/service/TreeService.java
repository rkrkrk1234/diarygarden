package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.dto.TreeRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Tree;
import com.GDG.worktree.team2.gardening_diary.repository.TreeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 나무 서비스
 */
@Service
public class TreeService {
    
    @Autowired
    private TreeRepository treeRepository;
    
    /**
     * 나무 생성
     */
    public Tree createTree(String userId, TreeRequest request) throws ExecutionException, InterruptedException {
        Tree tree = new Tree(
            userId,
            request.getWeekShortDate(),
            request.getWeekEndDate(),
            request.getDiaryLeafColors(),
            request.getTreeSnapshot(),
            request.getStatus()
        );
        
        return treeRepository.save(tree);
    }
    
    /**
     * 나무 조회 (ID로)
     */
    public Tree getTreeById(String id) throws ExecutionException, InterruptedException {
        return treeRepository.findById(id);
    }
    
    /**
     * 사용자의 모든 나무 조회
     */
    public List<Tree> getUserTrees(String userId) throws ExecutionException, InterruptedException {
        return treeRepository.findByUserId(userId);
    }
    
    /**
     * 사용자 ID와 상태로 나무 조회
     */
    public List<Tree> getUserTreesByStatus(String userId, String status) throws ExecutionException, InterruptedException {
        return treeRepository.findByUserIdAndStatus(userId, status);
    }
    
    /**
     * 나무 수정
     */
    public Tree updateTree(String treeId, String userId, TreeRequest request) 
            throws ExecutionException, InterruptedException {
        
        // 나무 존재 및 소유권 확인
        Tree existingTree = treeRepository.findById(treeId);
        if (existingTree == null) {
            throw new IllegalArgumentException("나무를 찾을 수 없습니다");
        }
        
        if (!userId.equals(existingTree.getUserId())) {
            throw new IllegalArgumentException("나무 수정 권한이 없습니다");
        }
        
        // 필드 업데이트
        if (request.getWeekShortDate() != null) {
            existingTree.setWeekShortDate(request.getWeekShortDate());
        }
        if (request.getWeekEndDate() != null) {
            existingTree.setWeekEndDate(request.getWeekEndDate());
        }
        if (request.getDiaryLeafColors() != null) {
            existingTree.setDiaryLeafColors(request.getDiaryLeafColors());
        }
        if (request.getTreeSnapshot() != null) {
            existingTree.setTreeSnapshot(request.getTreeSnapshot());
        }
        if (request.getStatus() != null) {
            existingTree.setStatus(request.getStatus());
        }
        
        return treeRepository.save(existingTree);
    }
    
    /**
     * 나무 삭제
     */
    public boolean deleteTree(String treeId, String userId) 
            throws ExecutionException, InterruptedException {
        
        // 나무 존재 및 소유권 확인
        Tree tree = treeRepository.findById(treeId);
        if (tree == null) {
            throw new IllegalArgumentException("나무를 찾을 수 없습니다");
        }
        
        if (!userId.equals(tree.getUserId())) {
            throw new IllegalArgumentException("나무 삭제 권한이 없습니다");
        }
        
        treeRepository.deleteById(treeId);
        return true;
    }
}







