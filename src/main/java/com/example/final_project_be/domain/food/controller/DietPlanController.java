package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.entity.DietPlans;
import com.example.final_project_be.domain.food.service.DietPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/dietplans")
public class DietPlanController {

    private final DietPlanService dietPlanService;

    @Autowired
    public DietPlanController(DietPlanService dietPlanService) {
        this.dietPlanService = dietPlanService;
    }

    // 식단 계획을 조회 (dietType, userGender에 따라)
    @GetMapping("/plan")
    public Optional<DietPlans> getDietPlan(@RequestParam String dietType, @RequestParam String userGender) {
        return dietPlanService.getDietPlan(dietType, userGender);
    }
}
