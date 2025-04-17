package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.MealRecordRequestDTO;
import com.example.final_project_be.domain.food.dto.MealRecordResponseDTO;
import com.example.final_project_be.domain.food.entity.MealRecords;
import com.example.final_project_be.domain.food.repository.MealRecordRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealRecordService {

    private final MealRecordRepository mealRecordRepository;
    private final MemberRepository memberRepository;

    public List<MealRecordResponseDTO> getTodayMeals(Long memberId) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        return mealRecordRepository.findMealRecordsDTOByMemberIdAndCreatedAtBetween(
                memberId, startOfDay, endOfDay);
    }

    public List<MealRecordResponseDTO> getWeeklyMeals(Long memberId) {
        LocalDateTime startOfWeek = LocalDate.now().minusDays(7).atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

        return mealRecordRepository.findMealRecordsDTOByMemberIdAndCreatedAtBetween(
                memberId, startOfWeek, endOfDay);
    }

    @Transactional
    public MealRecordResponseDTO saveMealRecord(MealRecordRequestDTO requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        MealRecords mealRecord = MealRecords.builder()
                .member(member)
                .foodName(requestDTO.getFoodName())
                .portion(requestDTO.getPortion())
                .unit(requestDTO.getUnit())
                .mealType(requestDTO.getMealType())
                .calories(requestDTO.getCalories())
                .protein(requestDTO.getProtein())
                .carbs(requestDTO.getCarbs())
                .fat(requestDTO.getFat())
                .build();

        MealRecords savedRecord = mealRecordRepository.save(mealRecord);
        return convertToDTO(savedRecord);
    }

    private MealRecordResponseDTO convertToDTO(MealRecords record) {
        return MealRecordResponseDTO.builder()
                .Name(record.getFoodName())
                .Calories(record.getCalories())
                .Protein(record.getProtein())
                .Carbs(record.getCarbs())
                .Fat(record.getFat())
                .build();
    }
}
