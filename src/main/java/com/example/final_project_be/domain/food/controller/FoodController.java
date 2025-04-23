package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.dto.MealRecordRequest;
import com.example.final_project_be.domain.food.dto.MealRecordResponse;
import com.example.final_project_be.domain.food.dto.RecommendedDietPlanRequest;
import com.example.final_project_be.domain.food.dto.UserDietInfoRequest;
import com.example.final_project_be.domain.food.dto.UserDietInfoResponse;
import com.example.final_project_be.domain.food.service.MealRecordService;
import com.example.final_project_be.domain.food.service.RecommendedDietPlanService;
import com.example.final_project_be.domain.food.service.UserDietInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Food API", description = "식사 기록 및 식단 정보 관련 API")
@RestController
@RequestMapping("/api/food")
@RequiredArgsConstructor
public class FoodController {
    private final MealRecordService mealRecordService;
    private final UserDietInfoService userDietInfoService;
    private final RecommendedDietPlanService recommendedDietPlanService;

    @Operation(summary = "식사 기록 저장", description = "사용자의 식사 기록을 저장합니다.")
    @PostMapping("/insert-meal")
    public ResponseEntity<MealRecordResponse> insertMeal(@RequestBody MealRecordRequest request) {
        return ResponseEntity.ok(mealRecordService.insertMeal(request));
    }

    @Operation(summary = "식사 기록 수정", description = "사용자의 식사 기록을 수정합니다.")
    @PutMapping("/update-meal")
    public ResponseEntity<MealRecordResponse> updateMeal(@RequestBody MealRecordRequest request) {
        System.out.println("request : " + request);
        System.out.println(mealRecordService.updateMeal(request));
        System.out.println("updateMeal 호출");
        return ResponseEntity.ok(mealRecordService.updateMeal(request));
    }

    @Operation(summary = "사용자 식단 정보 저장", description = "사용자의 식단에 필요한한 정보와 목표를 저장합니다.")
    @PostMapping("/user/diet-info")
    public ResponseEntity<UserDietInfoResponse> saveUserDietInfo(@RequestBody UserDietInfoRequest request) {
        return ResponseEntity.ok(userDietInfoService.saveUserDietInfo(request));
    }

    @Operation(summary = "사용자 식단 정보 수정", description = "사용자의 식단에 필요한한 정보와 목표를 저장합니다.")
    @PutMapping("/user/diet-info")
    public ResponseEntity<UserDietInfoResponse> updateUserDietInfo(@RequestBody UserDietInfoRequest request) {
        return ResponseEntity.ok(userDietInfoService.saveUserDietInfo(request));
    }
    @Operation(summary = "식단 조회", description = "사용자 ID와 날짜 범위에 따른 식단 정보를 조회합니다.")
    @GetMapping("/user/diet-info")
    public ResponseEntity<UserDietInfoResponse> getUserDietInfo(
            @RequestParam("memberId") Long memberId,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        UserDietInfoResponse response = mealRecordService.getUserDietInfo(memberId, startDate, endDate);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "추천 식단 계획 저장", description = "사용자에게 추천된 식단 계획을 저장합니다.")
    @PostMapping("/recommended-diet-plan")
    public ResponseEntity<?> saveRecommendedDietPlan(@RequestBody RecommendedDietPlanRequest request) {
        System.out.println("request : " + request);
        recommendedDietPlanService.saveRecommendedDietPlan(request);
        return ResponseEntity.ok().body(Map.of("status", "success")); 
    }
}