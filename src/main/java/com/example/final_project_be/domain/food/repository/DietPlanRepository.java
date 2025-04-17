package com.example.final_project_be.domain.food.repository;

 import com.example.final_project_be.domain.food.dto.DietRecommendationResponseDTO;
 import com.example.final_project_be.domain.food.entity.DietPlans;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

 import java.util.List;

public interface DietPlanRepository extends JpaRepository<DietPlans, Long> {

    @Query(value = "SELECT * FROM diet_plans WHERE modified_at = :dietType AND user_gender = :userGender ORDER BY RANDOM() LIMIT 5", nativeQuery = true)
    List<DietRecommendationResponseDTO> getDietPlan(@Param("dietType") String dietType, @Param("userGender") String userGender);
}
