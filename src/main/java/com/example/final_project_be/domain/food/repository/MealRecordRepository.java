package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.dto.MealRecordResponseDTO;
import com.example.final_project_be.domain.food.entity.MealRecords;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MealRecordRepository extends JpaRepository<MealRecords, Long> {

    @Query("SELECT new com.example.final_project_be.domain.food.dto.MealRecordResponseDTO(" +
            "m.id, m.member.id, m.foodName, m.portion, m.unit, m.mealType, " +
            "m.calories, m.protein, m.carbs, m.fat, m.createdAt) " +
            "FROM MealRecords m " +
            "WHERE m.member.id = :memberId " +
            "AND m.createdAt BETWEEN :startDateTime AND :endDateTime")
    List<MealRecordResponseDTO> findMealRecordsDTOByMemberIdAndCreatedAtBetween(
            @Param("memberId") Long memberId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime);
}
