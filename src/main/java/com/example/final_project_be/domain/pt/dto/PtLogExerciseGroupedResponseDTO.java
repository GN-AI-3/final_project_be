package com.example.final_project_be.domain.pt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtLogExerciseGroupedResponseDTO {
    private LocalDateTime startTime;
    private List<ExerciseDetailDTO> exercises;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseDetailDTO {
        private Long exerciseId;
        private String exerciseName;
        private String feedback;
        private Integer reps;
        private Integer sets;
        private Integer weight;
    }
} 