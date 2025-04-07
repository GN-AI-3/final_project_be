package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.entity.MealRecords;
import com.example.final_project_be.domain.food.repository.MealRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;

    @Autowired
    public MealRecordService(MealRecordRepository mealRecordRepository) {
        this.mealRecordRepository = mealRecordRepository;
    }

    public List<MealRecords> getTodayMeals(Long userId) {
        return mealRecordRepository.findTodayMeals(userId);
    }

    public List<MealRecords> getWeeklyMeals(Long userId) {
        return mealRecordRepository.findWeeklyMeals(userId);
    }

    public MealRecords saveMealRecord(MealRecords mealRecord) {
        return mealRecordRepository.save(mealRecord);
    }
}
