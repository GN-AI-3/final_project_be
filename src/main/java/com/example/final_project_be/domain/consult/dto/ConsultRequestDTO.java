package com.example.final_project_be.domain.consult.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultRequestDTO {

    @NotNull(message = "PT 계약 ID는 필수 입력값입니다.")
    private Long ptContractId;

    // 1. 기본 정보
    private String job;
    private String lifestyle;
    private String medicalHistory;
    private String trainingGoal;
    private LocalDate goalDeadline;
    private LocalDate consultationDate;
    private String consultationChannel;
    private String consultantName;
    private String consultationNotes;

    // 2. 운동 정보
    private Boolean hasExperience;
    private String exerciseExperience;
    private Integer weeklyWorkoutFrequency;
    private List<String> preferredExercises;
    private List<String> dislikedExercises;
    private String weakPointsOrPain;
    private List<String> bodyConcerns;

    // 3. 식단 정보
    private Boolean needsDietPlan;

    // 4. 일정 정보
    private List<String> preferredDays;
    private List<String> preferredTimes;
    private Integer availableSessionsPerWeek;
    private String distanceToGym;
} 