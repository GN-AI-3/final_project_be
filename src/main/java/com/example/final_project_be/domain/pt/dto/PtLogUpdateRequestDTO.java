package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PtLogUpdateRequestDTO {
    private String feedback;

    @NotNull
    private Boolean injuryCheck;

    private String nextPlan;

    @Valid
    private List<PtLogExerciseUpdateRequestDTO> exercises;
} 