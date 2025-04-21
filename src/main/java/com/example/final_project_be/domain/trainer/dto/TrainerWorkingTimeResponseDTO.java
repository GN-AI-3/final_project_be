package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkingTimeResponseDTO {
    private DayOfWeek day;
    private String startTime;
    private String endTime;
    private boolean isActive;
} 