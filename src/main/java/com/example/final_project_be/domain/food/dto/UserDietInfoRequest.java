package com.example.final_project_be.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDietInfoRequest {
    private Long memberId;
    private String allergies;
    private String foodPreferences;
    private String mealPattern;
    private String activityLevel;
    private String specialRequirements;
}