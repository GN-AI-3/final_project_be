package com.example.final_project_be.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MealRecordResponse {
    private String status;
    private String food;
    private String mealType;
    private Double portion;
    private String unit;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
}