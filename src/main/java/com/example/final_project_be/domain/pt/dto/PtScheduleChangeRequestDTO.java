package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtScheduleChangeRequestDTO {
    @NotNull(message = "시작 시간은 필수입니다.")
    private Long startTime;
    private Long endTime;
    private String reason;
} 