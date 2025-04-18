package com.example.final_project_be.domain.trainer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrainerWorkingTimeUpdateResponseDTO {
    private Long trainerId;
    private List<Integer> updatedDays;
    private String message;
} 