package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PtLogExerciseUpdateRequestDTO {
    private Integer sequence;

    @Min(value = 1, message = "세트 수는 1 이상이어야 합니다.")
    private Integer sets;

    @Min(value = 1, message = "반복 횟수는 1 이상이어야 합니다.")
    private Integer reps;

    @Min(value = 0, message = "무게는 0 이상이어야 합니다.")
    private Integer weight;

    private Integer restTime;

    private String feedback;
} 