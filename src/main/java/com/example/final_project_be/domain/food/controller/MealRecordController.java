package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.dto.MealRecordRequestDTO;
import com.example.final_project_be.domain.food.dto.MealRecordResponseDTO;
import com.example.final_project_be.domain.food.service.MealRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meal_records")
@Tag(name = "Meal Records", description = "식사 기록 관련 API")
public class MealRecordController {

    private final MealRecordService mealRecordService;

    @GetMapping("/today/{memberId}")
    @Operation(summary = "오늘의 식사 기록 조회", description = "사용자의 오늘 날짜 식사 기록을 조회합니다.")
    public ResponseEntity<List<MealRecordResponseDTO>> getTodayMeals(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long memberId) {
        return ResponseEntity.ok(mealRecordService.getTodayMeals(memberId));
    }

    @GetMapping("/weekly/{memberId}")
    @Operation(summary = "최근 7일간 식사 기록 조회", description = "사용자의 최근 일주일 식사 기록을 조회합니다.")
    public ResponseEntity<List<MealRecordResponseDTO>> getWeeklyMeals(
            @Parameter(description = "사용자 ID", example = "1") @PathVariable Long memberId) {
        return ResponseEntity.ok(mealRecordService.getWeeklyMeals(memberId));
    }

    @PostMapping("")
    @Operation(summary = "식사 기록 저장", description = "새로운 식사 기록을 저장합니다.")
    public ResponseEntity<MealRecordResponseDTO> saveMealRecord(
            @Parameter(description = "저장할 식사 기록 객체") @RequestBody MealRecordRequestDTO requestDTO) {
        return ResponseEntity.ok(mealRecordService.saveMealRecord(requestDTO));
    }
}
