package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.DietRecommendationRequestDTO;
import com.example.final_project_be.domain.food.dto.DietRecommendationResponseDTO;
import com.example.final_project_be.domain.food.repository.DietPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DietPlanService {

    private final DietPlanRepository dietPlanRepository;


    public List<DietRecommendationResponseDTO> getDietPlan(DietRecommendationRequestDTO dietTypeDTO ){
        return dietPlanRepository.getDietPlan(dietTypeDTO.getDietType(), dietTypeDTO.getGoal());
    }
}
