package com.example.final_project_be.domain.report.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {
    private Long ptContractId;
    private JsonNode exerciseReport;
    private JsonNode dietReport;
    private JsonNode inbodyReport;
} 