package com.example.final_project_be.domain.food.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietRecommendationResponseDTO {
    private String dietType;
    private Map<String, List<RecommendedMealDto>> mealPlan; // 아침, 점심, 저녁 별 리스트
    private Map<String, Double> nutritionGoals;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
class RecommendedMealDto {
    private String foodName;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private String unit;
}
