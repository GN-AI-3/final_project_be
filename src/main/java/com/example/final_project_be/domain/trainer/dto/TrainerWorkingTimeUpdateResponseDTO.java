package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrainerWorkingTimeUpdateResponseDTO {
    private Long trainerId;
    private List<DayOfWeek> updatedDays;
    private String message;
} 