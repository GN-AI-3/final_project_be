package com.example.final_project_be.domain.trainer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrainerUnavailableTimeResponseDTO {
    private Long id;
    private Long trainerId;
    private Long startTime;
    private Long endTime;
    private String reason;
    private Long createdAt;
} 