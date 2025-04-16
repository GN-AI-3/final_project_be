package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.service.FoodNutritionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/food_nutritions")
@Tag(name = "Food Nutrition", description = "음식 영양 정보 관련 API")
public class FoodNutritionController {

    private final FoodNutritionService foodNutritionService;

    @GetMapping("")
    @Operation(summary = "음식 영양 정보 조회", description = "음식 이름을 기준으로 해당 음식의 영양 정보를 조회합니다.")
    public ResponseEntity<?> getFoodNutrition(
            @Parameter(description = "음식 이름", example = "닭가슴살")
            @RequestParam String foodName
    ) {
        return foodNutritionService.getFoodNutritionByName(foodName)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
