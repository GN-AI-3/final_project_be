package com.example.final_project_be.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MealRecordResponseDTO {

    private String Name;

    private Double Calories;

    private Double Protein;

    private Double Carbs;

    private Double Fat;
}
