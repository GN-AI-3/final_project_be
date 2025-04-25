package com.example.final_project_be.domain.pt.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PtScheduleCreateRequestDTO {
    @NotNull(message = "PT 계약 ID는 필수입니다.")
    private Long ptContractId;

    @NotNull(message = "시작 시간은 필수입니다.")
    private Long startTime;

    private Long endTime;
} 