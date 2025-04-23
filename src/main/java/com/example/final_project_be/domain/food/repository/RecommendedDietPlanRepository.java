package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.entity.RecommendedDietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendedDietPlanRepository extends JpaRepository<RecommendedDietPlan, Long> {
}