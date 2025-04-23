package com.example.final_project_be.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class RecommendedDietPlanRequest {
    private Long memberId;
    private String planScope;
    private String planSummary;
    private Map<String, Map<String, String>> planJson;
}