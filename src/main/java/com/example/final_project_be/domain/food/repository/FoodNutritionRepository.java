package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.dto.MealRecordResponseDTO;
import com.example.final_project_be.domain.food.entity.FoodNutrition;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FoodNutritionRepository extends JpaRepository<FoodNutrition, Long> {

    @EntityGraph(attributePaths = {"foodPreferences"})  // 예시로 foodPreferences 필드를 연관 로딩
    @Query("select f from FoodNutrition f where lower(f.Name) like lower(concat('%', :foodName, '%'))")
    Optional<MealRecordResponseDTO> findByNameContainingIgnoreCase(@Param("foodName") String foodName);
}
