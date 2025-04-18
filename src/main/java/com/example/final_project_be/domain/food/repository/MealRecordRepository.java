package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.entity.MealRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MealRecordRepository extends JpaRepository<MealRecord, Long> {
    Optional<MealRecord> findByMemberIdAndFoodNameAndMealTypeAndMealDate(
        Long memberId, String foodName, String mealType, LocalDate mealDate);
}