package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PtLogCreateRequestDTO {
    @NotNull(message = "PT 스케줄 ID는 필수입니다.")
    private Long ptScheduleId;

    private String feedback;
    private boolean injuryCheck;
    private String nextPlan;

    private List<ExerciseLogDTO> exercises;

    @Getter
    @Setter
    public static class ExerciseLogDTO {
        @NotNull(message = "운동 ID는 필수입니다.")
        private Long exerciseId;

        @NotNull(message = "세트 수는 필수입니다.")
        private Integer sets;

        @NotNull(message = "반복 횟수는 필수입니다.")
        private Integer reps;

        @NotNull(message = "무게는 필수입니다.")
        private Integer weight;

        private Integer restTime;
        private String correctionNote;
        private String feedback;
    }
} 