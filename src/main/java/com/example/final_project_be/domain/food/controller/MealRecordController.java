package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.entity.MealRecords;
import com.example.final_project_be.domain.food.service.MealRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mealrecords")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @Autowired
    public MealRecordController(MealRecordService mealRecordService) {
        this.mealRecordService = mealRecordService;
    }

    // 오늘의 식사 기록을 조회
    @GetMapping("/today/{userId}")
    public List<MealRecords> getTodayMeals(@PathVariable Long userId) {
        return mealRecordService.getTodayMeals(userId);
    }

    // 최근 7일 간의 식사 기록을 조회
    @GetMapping("/weekly/{userId}")
    public List<MealRecords> getWeeklyMeals(@PathVariable Long userId) {
        return mealRecordService.getWeeklyMeals(userId);
    }

    // 새로운 식사 기록 저장
    @PostMapping("/save")
    public MealRecords saveMealRecord(@RequestBody MealRecords mealRecord) {
        return mealRecordService.saveMealRecord(mealRecord);
    }
}
