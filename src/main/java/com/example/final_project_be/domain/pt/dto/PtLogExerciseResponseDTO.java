package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PtLogExerciseResponseDTO {
    @Schema(hidden = true)
    private Long id;
    @Schema(hidden = true)
    private Long exerciseId;
    private String exerciseName;
    @Schema(hidden = true)
    private Integer sequence;
    private Integer sets;
    private Integer reps;
    private Integer weight;
    private Integer restTime;
    private String feedback;

    public static PtLogExerciseResponseDTO from(PtLogExercise ptLogExercise) {
        Exercise exercise = ptLogExercise.getExercise();
        return PtLogExerciseResponseDTO.builder()
                .exerciseName(exercise.getName())
                .sets(ptLogExercise.getSets())
                .reps(ptLogExercise.getReps())
                .weight(ptLogExercise.getWeight())
                .restTime(ptLogExercise.getRestTime())
                .feedback(ptLogExercise.getFeedback())
                .build();
    }
} 