package com.example.final_project_be.domain.food.repository;

 import com.example.final_project_be.domain.food.entity.DietPlans;
 import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DietPlanRepository extends JpaRepository<DietPlans, Long> {

    @Query("SELECT d FROM DietPlans d WHERE d.modifiedAt = :dietType AND d.UserGender = :userGender ORDER BY RANDOM() LIMIT 1")
    Optional<DietPlans> getDietPlan(@Param("dietType") String dietType, @Param("userGender") String userGender);
}
