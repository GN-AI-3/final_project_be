package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.entity.MemberDietInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberDietInfoRepository extends JpaRepository<MemberDietInfo, Long> {

    @Modifying
    @Query(value = """
        INSERT INTO member_diet_info (
            member_id, allergies, food_preferences,
            meal_pattern, activity_level, special_requirements, is_deleted,food_avoidance
        )
        VALUES (
            :memberId, :allergies, :foodPreferences,
            :mealPattern, :activityLevel, :specialRequirements, false
        )
        ON CONFLICT (member_id) DO UPDATE SET
            allergies = EXCLUDED.allergies,
            food_preferences = EXCLUDED.food_preferences,
            meal_pattern = EXCLUDED.meal_pattern,
            activity_level = EXCLUDED.activity_level,
            special_requirements = EXCLUDED.special_requirements,
            food_avoidance = EXCLUDED.food_avoidance,
            is_deleted = false
        """, nativeQuery = true)
    void upsertMemberDietInfo(
            @Param("memberId") Long memberId,
            @Param("allergies") String allergies,
            @Param("foodPreferences") String foodPreferences,
            @Param("mealPattern") String mealPattern,
            @Param("activityLevel") String activityLevel,
            @Param("specialRequirements") String specialRequirements,
            @Param("foodAvoidance") String foodAvoidance);  

    Optional<MemberDietInfo> findByMemberId(Long memberId);
}