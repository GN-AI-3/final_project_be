package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.MealRecordResponseDTO;
import com.example.final_project_be.domain.food.repository.FoodNutritionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@RequiredArgsConstructor
@Service
public class FoodNutritionService {

    private final FoodNutritionRepository foodNutritionRepository;


    public Optional<MealRecordResponseDTO> getFoodNutritionByName(String foodName) {
        return foodNutritionRepository.findByNameContainingIgnoreCase(foodName);
    }
}
