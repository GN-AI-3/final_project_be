package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.MealRecordRequest;
import com.example.final_project_be.domain.food.dto.MealRecordResponse;
import com.example.final_project_be.domain.food.entity.MealRecord;
import com.example.final_project_be.domain.food.repository.MealRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MealRecordService {
    private final MealRecordRepository mealRecordRepository;

    @Transactional
    public MealRecordResponse recordMeal(MealRecordRequest request) {
        // TODO: Implement meal parsing logic
        // TODO: Implement nutrition lookup logic

        MealRecord mealRecord = new MealRecord();
        mealRecord.setMemberId(request.getMemberId());
        mealRecord.setFoodName("parsed_food_name");
        mealRecord.setMealType("parsed_meal_type");
        mealRecord.setPortion(100.0);
        mealRecord.setUnit("g");
        mealRecord.setMealDate(LocalDate.now());
        mealRecord.setCalories(0.0);
        mealRecord.setProtein(0.0);
        mealRecord.setCarbs(0.0);
        mealRecord.setFat(0.0);

        MealRecord savedRecord = mealRecordRepository.save(mealRecord);

        return MealRecordResponse.builder()
                .status("✅ 저장 완료")
                .food(savedRecord.getFoodName())
                .mealType(savedRecord.getMealType())
                .portion(savedRecord.getPortion())
                .unit(savedRecord.getUnit())
                .calories(savedRecord.getCalories())
                .protein(savedRecord.getProtein())
                .carbs(savedRecord.getCarbs())
                .fat(savedRecord.getFat())
                .build();
    }

    @Transactional
    public MealRecordResponse insertMeal(MealRecordRequest request) {
        MealRecord mealRecord = new MealRecord();
        mealRecord.setMemberId(request.getMemberId());
        mealRecord.setFoodName(request.getFoodName());
        mealRecord.setMealType(request.getMealType());
        mealRecord.setPortion(request.getPortion());
        mealRecord.setUnit(request.getUnit());
        mealRecord.setMealDate(LocalDate.now());
        mealRecord.setCalories(request.getCalories());
        mealRecord.setProtein(request.getProtein());
        mealRecord.setCarbs(request.getCarbs());
        mealRecord.setFat(request.getFat());
        
        MealRecord savedRecord = mealRecordRepository.save(mealRecord);

        return MealRecordResponse.builder()
                .status("✅ 저장 완료")
                .food(savedRecord.getFoodName())
                .mealType(savedRecord.getMealType())
                .portion(savedRecord.getPortion())
                .unit(savedRecord.getUnit())
                .calories(savedRecord.getCalories())
                .protein(savedRecord.getProtein())
                .carbs(savedRecord.getCarbs())
                .fat(savedRecord.getFat())
                .build();
    }

    @Transactional
    public MealRecordResponse updateMeal(MealRecordRequest request) {
        LocalDate today = LocalDate.now();

        MealRecord mealRecord = mealRecordRepository.findByMemberIdAndFoodNameAndMealTypeAndMealDate(
        request.getMemberId(), request.getFoodName(), request.getMealType(), today)
    .orElseThrow(() -> new IllegalArgumentException("오늘의 해당 식사 기록이 존재하지 않습니다."));

        mealRecord.setPortion(request.getPortion());
        mealRecord.setUnit(request.getUnit());
        mealRecord.setCalories(request.getCalories());
        mealRecord.setProtein(request.getProtein());
        mealRecord.setCarbs(request.getCarbs());
        mealRecord.setFat(request.getFat());

        MealRecord updatedRecord = mealRecordRepository.save(mealRecord);

        return MealRecordResponse.builder()
                .status("♻️ 기존 기록 갱신 완료")
                .food(updatedRecord.getFoodName())
                .mealType(updatedRecord.getMealType())
                .portion(updatedRecord.getPortion())
                .unit(updatedRecord.getUnit())
                .calories(updatedRecord.getCalories())
                .protein(updatedRecord.getProtein())
                .carbs(updatedRecord.getCarbs())
                .fat(updatedRecord.getFat())
                .build();
    }
}