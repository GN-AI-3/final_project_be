package com.example.final_project_be.domain.food.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodNutritionResponseDTO {
    private String name;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private String servingUnit;
}
