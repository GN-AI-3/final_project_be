package com.example.final_project_be.domain.food.dto;

import com.example.final_project_be.domain.food.entity.MealRecords;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordRequestDTO {
    private Long memberId;
    private String foodName;
    private Double portion;
    private String unit;
    private MealRecords.MealType mealType;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
}
