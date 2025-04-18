package com.example.final_project_be.domain.trainer.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrainerUnavailableTimeResponseDTO {
    private Long id;
    private Long trainerId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private LocalDateTime createdAt;
} 