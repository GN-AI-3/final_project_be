package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.entity.DietPlans;
import com.example.final_project_be.domain.food.repository.DietPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;

    @Autowired
    public DietPlanService(DietPlanRepository dietPlanRepository) {
        this.dietPlanRepository = dietPlanRepository;
    }

    public Optional<DietPlans> getDietPlan(String dietType, String userGender) {
        return dietPlanRepository.getDietPlan(dietType, userGender);
    }
}
