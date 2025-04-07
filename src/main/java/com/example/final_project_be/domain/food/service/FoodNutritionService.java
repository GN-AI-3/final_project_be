package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.entity.FoodNutrition;
import com.example.final_project_be.domain.food.repository.FoodNutritionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class FoodNutritionService {

    private final FoodNutritionRepository foodNutritionRepository;

    @Autowired
    public FoodNutritionService(FoodNutritionRepository foodNutritionRepository) {
        this.foodNutritionRepository = foodNutritionRepository;
    }

    public Optional<FoodNutrition> getFoodNutritionByName(String foodName) {
        return foodNutritionRepository.findByNameContainingIgnoreCase(foodName);
    }
}
