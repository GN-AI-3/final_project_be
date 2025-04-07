package com.example.final_project_be.domain.food.repository;

import com.example.final_project_be.domain.food.entity.MealRecords;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface MealRecordRepository extends CrudRepository<MealRecords, Long> {

    // 오늘의 식사 기록 조회 (주어진 userId로 오늘의 식사만 조회)
    @Query("SELECT m FROM MealRecords m WHERE m.id = :userId AND DATE(m.createdAt) = CURRENT_DATE ORDER BY m.createdAt")
    List<MealRecords> findTodayMeals(Long userId);

    // 주간 식사 기록 조회 (최근 7일 간의 식사 기록)
    @Query("SELECT m FROM MealRecords m WHERE m.id = :userId AND m.createdAt >= CURRENT_DATE - 7 ORDER BY m.createdAt DESC")
    List<MealRecords> findWeeklyMeals(Long userId);

    // 특정 사용자의 식사 기록을 저장 (자동으로 `CrudRepository`에서 제공됨)
    MealRecords save(@NotNull MealRecords mealRecord);
}
