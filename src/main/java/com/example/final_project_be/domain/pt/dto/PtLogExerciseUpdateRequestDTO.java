package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtLogExerciseUpdateRequestDTO {
    private Long id;

    @NotNull
    private Long exerciseId;

    @Min(1)
    private Integer sets;

    @Min(1)
    private Integer reps;

    @Min(0)
    private Integer weight;

    @Min(0)
    private Integer restTime;

    private String feedback;
} 