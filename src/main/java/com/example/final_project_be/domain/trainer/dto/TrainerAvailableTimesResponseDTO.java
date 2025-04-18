package com.example.final_project_be.domain.trainer.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TrainerAvailableTimesResponseDTO {
    private Long trainerId;
    private Long startTime;
    private Long endTime;
    private Integer sessionMinutes;
    private List<AvailableTimeSlot> availableTimes;

    @Getter
    @Builder
    public static class AvailableTimeSlot {
        private Long startTime;
        private Long endTime;
    }
} 