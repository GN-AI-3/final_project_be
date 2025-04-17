package com.example.final_project_be.domain.pt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "PT 계약 수정 요청 DTO")
public class PtContractUpdateRequestDTO {

    @Schema(description = "계약 종료일")
    private Long endDate;

    @Schema(description = "PT 계약 관련 메모")
    private String memo;

    @Schema(description = "총 PT 횟수")
    @Min(value = 1, message = "총 PT 횟수는 1회 이상이어야 합니다.")
    private Integer totalCount;
} 