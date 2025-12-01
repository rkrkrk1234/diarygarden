package com.GDG.worktree.team2.gardening_diary.service;

import com.GDG.worktree.team2.gardening_diary.dto.GardenRequest;
import com.GDG.worktree.team2.gardening_diary.entity.Garden;
import com.GDG.worktree.team2.gardening_diary.repository.GardenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 정원 서비스
 */
@Service
public class GardenService {
    
    @Autowired
    private GardenRepository gardenRepository;
    
    /**
     * 정원 생성 또는 업데이트
     */
    public Garden createOrUpdateGarden(String userId, GardenRequest request) throws ExecutionException, InterruptedException {
        // 사용자의 정원 조회
        List<Garden> gardens = gardenRepository.findByUserId(userId);
        
        Garden garden;
        if (gardens != null && !gardens.isEmpty()) {
            // 기존 정원이 있으면 업데이트
            garden = gardens.get(0);
            garden.setTreeCount(request.getTreeCount());
        } else {
            // 없으면 새로 생성
            garden = new Garden(userId, request.getTreeCount());
        }
        
        return gardenRepository.save(garden);
    }
    
    /**
     * 정원 조회 (ID로)
     */
    public Garden getGardenById(String id) throws ExecutionException, InterruptedException {
        return gardenRepository.findById(id);
    }
    
    /**
     * 사용자의 정원 조회
     */
    public Garden getUserGarden(String userId) throws ExecutionException, InterruptedException {
        List<Garden> gardens = gardenRepository.findByUserId(userId);
        if (gardens != null && !gardens.isEmpty()) {
            return gardens.get(0);
        }
        return null;
    }
    
    /**
     * 정원 수정
     */
    public Garden updateGarden(String gardenId, String userId, GardenRequest request) 
            throws ExecutionException, InterruptedException {
        
        // 정원 존재 및 소유권 확인
        Garden existingGarden = gardenRepository.findById(gardenId);
        if (existingGarden == null) {
            throw new IllegalArgumentException("정원을 찾을 수 없습니다");
        }
        
        if (!userId.equals(existingGarden.getUserId())) {
            throw new IllegalArgumentException("정원 수정 권한이 없습니다");
        }
        
        // 필드 업데이트
        if (request.getTreeCount() != null) {
            existingGarden.setTreeCount(request.getTreeCount());
        }
        
        return gardenRepository.save(existingGarden);
    }
    
    /**
     * 정원 삭제
     */
    public boolean deleteGarden(String gardenId, String userId) 
            throws ExecutionException, InterruptedException {
        
        // 정원 존재 및 소유권 확인
        Garden garden = gardenRepository.findById(gardenId);
        if (garden == null) {
            throw new IllegalArgumentException("정원을 찾을 수 없습니다");
        }
        
        if (!userId.equals(garden.getUserId())) {
            throw new IllegalArgumentException("정원 삭제 권한이 없습니다");
        }
        
        gardenRepository.deleteById(gardenId);
        return true;
    }
}





