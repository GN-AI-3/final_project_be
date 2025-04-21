package com.example.final_project_be.domain.report.dto;

import java.time.LocalDateTime;

import com.example.final_project_be.domain.report.entity.Report;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponseDTO {
    private JsonNode exerciseReport;
    private JsonNode dietReport;
    private JsonNode inbodyReport;
    private LocalDateTime createdAt;

    public static ReportResponseDTO from(Report report) {
        return ReportResponseDTO.builder()
                .exerciseReport(report.getExerciseReport())
                .dietReport(report.getDietReport())
                .inbodyReport(report.getInbodyReport())
                .createdAt(report.getCreatedAt())
                .build();
    }
} 