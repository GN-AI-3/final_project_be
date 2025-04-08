package com.example.final_project_be.domain.food.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponseDTO {
    private String name;
    private String gender;
    private Double height;
    private Double weight;
    private LocalDate birth;
    private String goal;
    private String activityLevel;
    private List<String> allergies;
    private String dietaryPreference;
    private String mealPattern;
    private List<String> mealTimes;
    private List<String> foodPreferences;
    private List<String> specialRequirements;
}
