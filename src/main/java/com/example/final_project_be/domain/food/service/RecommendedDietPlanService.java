package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.RecommendedDietPlanRequest;
import com.example.final_project_be.domain.food.entity.RecommendedDietPlan;
import com.example.final_project_be.domain.food.repository.RecommendedDietPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class RecommendedDietPlanService {
    private final RecommendedDietPlanRepository recommendedDietPlanRepository;

    public void saveRecommendedDietPlan(RecommendedDietPlanRequest request) {
        Map<String, Map<String, String>> planJson = request.getPlanJson();

        if (planJson == null || planJson.isEmpty()) {
            throw new IllegalArgumentException("planJson이 비어있으면 저장할 수 없습니다.");
        }
        for (Map.Entry<String, Map<String, String>> entry : planJson.entrySet()) {
            String dayKey = entry.getKey(); // "single" 또는 "monday" 등
            Map<String, String> meals = entry.getValue();
        
            String breakfast = meals.getOrDefault("아침", meals.getOrDefault("breakfast", ""));
            String lunch = meals.getOrDefault("점심", meals.getOrDefault("lunch", ""));
            String dinner = meals.getOrDefault("저녁", meals.getOrDefault("dinner", ""));
        
            RecommendedDietPlan plan = new RecommendedDietPlan();
            plan.setMemberId(request.getMemberId());
            plan.setPlanScope(request.getPlanScope());
            plan.setPlanSummary(request.getPlanSummary());
            plan.setPlanDay(dayKey);
            plan.setBreakfastPlan(breakfast);
            plan.setLunchPlan(lunch);
            plan.setDinnerPlan(dinner);
            plan.setCreatedDatetime(LocalDateTime.now());
        
            recommendedDietPlanRepository.save(plan);
        }
    }
}
