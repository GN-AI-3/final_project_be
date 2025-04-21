package com.example.final_project_be.domain.report.dto;

import com.example.final_project_be.domain.report.entity.Report;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportResponseDTO {
    private Long id;
    private Long ptContractId;
    private JsonNode exerciseReport;
    private JsonNode dietReport;
    private JsonNode inbodyReport;

    public static ReportResponseDTO from(Report report) {
        ReportResponseDTO dto = new ReportResponseDTO();
        dto.setId(report.getId());
        dto.setPtContractId(report.getPtContract().getId());
        dto.setExerciseReport(report.getExerciseReport());
        dto.setDietReport(report.getDietReport());
        dto.setInbodyReport(report.getInbodyReport());
        return dto;
    }
} 