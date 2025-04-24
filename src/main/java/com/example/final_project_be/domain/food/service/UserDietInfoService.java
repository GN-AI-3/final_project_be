package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.UserDietInfoRequest;
import com.example.final_project_be.domain.food.dto.UserDietInfoResponse;
import com.example.final_project_be.domain.food.entity.MemberDietInfo;
import com.example.final_project_be.domain.food.repository.MemberDietInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserDietInfoService {
    private final MemberDietInfoRepository memberDietInfoRepository;

    @Transactional
    public UserDietInfoResponse saveUserDietInfo(UserDietInfoRequest request) {
        // 기존 정보 조회
        MemberDietInfo existingInfo = memberDietInfoRepository.findByMemberId(request.getMemberId())
                .orElseGet(() -> {
                    MemberDietInfo newInfo = new MemberDietInfo();
                    newInfo.setIsDeleted(false); // ✅ 기본값 false 설정
                    return newInfo;
                });

        // 새로운 정보가 있는 경우에만 업데이트
        if (request.getAllergies() != null && !request.getAllergies().isEmpty()) {
            existingInfo.setAllergies(request.getAllergies());
        }
        if (request.getFoodPreferences() != null && !request.getFoodPreferences().isEmpty()) {
            existingInfo.setFoodPreferences(request.getFoodPreferences());
        }
        if (request.getMealPattern() != null && !request.getMealPattern().isEmpty()) {
            existingInfo.setMealPattern(request.getMealPattern());
        }
        if (request.getActivityLevel() != null && !request.getActivityLevel().isEmpty()) {
            existingInfo.setActivityLevel(request.getActivityLevel());
        }
        if (request.getSpecialRequirements() != null && !request.getSpecialRequirements().isEmpty()) {
            existingInfo.setSpecialRequirements(request.getSpecialRequirements());
        }
        if (request.getFoodAvoidance() != null && !request.getFoodAvoidance().isEmpty()) {
            existingInfo.setFoodAvoidance(request.getFoodAvoidance());
        }
        // memberId 설정
        existingInfo.setMemberId(request.getMemberId());
        // ✅ 기존 데이터에 isDeleted 값이 null일 수 있으므로 명시적으로 다시 설정
        if (existingInfo.getIsDeleted() == null) {
            existingInfo.setIsDeleted(false);
        }
        // 저장
        MemberDietInfo savedInfo = memberDietInfoRepository.save(existingInfo);

        return UserDietInfoResponse.builder()
                .status("✅ 사용자 식단 정보 저장 완료")
                .allergies(savedInfo.getAllergies())
                .foodPreferences(savedInfo.getFoodPreferences())
                .mealPattern(savedInfo.getMealPattern())
                .activityLevel(savedInfo.getActivityLevel())
                .specialRequirements(savedInfo.getSpecialRequirements())
                .foodAvoidance(savedInfo.getFoodAvoidance())
                .build();
    }
}