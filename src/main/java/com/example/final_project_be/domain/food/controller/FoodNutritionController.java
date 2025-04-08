package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.entity.FoodNutrition;
import com.example.final_project_be.domain.food.service.FoodNutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/foodnutritions")
public class FoodNutritionController {

    private final FoodNutritionService foodNutritionService;

    @Autowired
    public FoodNutritionController(FoodNutritionService foodNutritionService) {
        this.foodNutritionService = foodNutritionService;
    }

    // 음식 이름을 기준으로 영양 정보를 조회
    @GetMapping("/nutrition")
    public Optional<FoodNutrition> getFoodNutrition(@RequestParam String foodName) {
        return foodNutritionService.getFoodNutritionByName(foodName);
    }
}
