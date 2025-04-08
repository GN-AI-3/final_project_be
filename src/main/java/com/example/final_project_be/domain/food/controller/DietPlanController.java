package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.dto.DietRecommendationRequestDTO;
import com.example.final_project_be.domain.food.dto.DietRecommendationResponseDTO;
import com.example.final_project_be.domain.food.service.DietPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dietplans")
@Tag(name = "Diet Plan", description = "식단 추천 관련 API")
public class DietPlanController {

    private final DietPlanService dietPlanService;

    @GetMapping("/plan")
    @Operation(summary = "식단 계획 조회", description = "식단 유형과 사용자 성별에 따라 식단 계획을 조회합니다.")
    public ResponseEntity<List<DietRecommendationResponseDTO>> getDietPlan(
            @RequestBody DietRecommendationRequestDTO dietTypeDTO) {
        return ResponseEntity.ok(dietPlanService.getDietPlan(dietTypeDTO));
    }
}
