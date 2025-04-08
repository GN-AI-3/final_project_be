package com.example.final_project_be.domain.food.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietRecommendationRequestDTO {
    private String goal;
    private String dietType;
}
