package com.example.final_project_be.domain.trainer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerUnavailableTimeCreateRequestDTO {
    @NotNull(message = "시작 시간은 필수입니다")
    private Long startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    private Long endTime;

    private String reason;
} 