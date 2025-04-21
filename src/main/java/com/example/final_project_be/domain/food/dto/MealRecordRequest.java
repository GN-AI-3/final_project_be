package com.example.final_project_be.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MealRecordRequest {
    
    private Long memberId;
    private String foodName;
    private String mealType;
    private Double portion;
    private String unit;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
}