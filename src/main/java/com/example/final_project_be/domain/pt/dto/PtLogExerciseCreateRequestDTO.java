package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PtLogExerciseCreateRequestDTO {
    @NotNull(message = "운동 ID는 필수입니다.")
    private Long exerciseId;

    @NotNull(message = "순서는 필수입니다.")
    @Min(value = 1, message = "순서는 1 이상이어야 합니다.")
    private Integer sequence;

    @NotNull(message = "세트 수는 필수입니다.")
    @Min(value = 1, message = "세트 수는 1 이상이어야 합니다.")
    private Integer sets;

    @NotNull(message = "반복 횟수는 필수입니다.")
    @Min(value = 1, message = "반복 횟수는 1 이상이어야 합니다.")
    private Integer reps;

    @NotNull(message = "무게는 필수입니다.")
    @Min(value = 0, message = "무게는 0 이상이어야 합니다.")
    private Integer weight;

    private Integer restTime;

    private String feedback;
} 