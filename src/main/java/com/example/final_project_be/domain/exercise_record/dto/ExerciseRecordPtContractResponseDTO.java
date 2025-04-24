package com.example.final_project_be.domain.exercise_record.dto;

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
public class ExerciseRecordPtContractResponseDTO {
    private LocalDate date;
    private List<ExerciseRecordDetailDTO> exercises;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseRecordDetailDTO {
        private Long exerciseId;
        private String exerciseName;
        private List<SetDetailDTO> sets;
        private String memo;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SetDetailDTO {
        private Integer reps;
        private Integer sets;
        private Double weight;
    }
} 