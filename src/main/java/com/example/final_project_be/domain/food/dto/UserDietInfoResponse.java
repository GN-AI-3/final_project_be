package com.example.final_project_be.domain.food.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDietInfoResponse {
    private String status;
    private String goal;
    private String gender;
    private String allergies;
    private String foodPreferences;
    private String mealPattern;
    private String activityLevel;
    private String specialRequirements;
    private String foodAvoidance;
}