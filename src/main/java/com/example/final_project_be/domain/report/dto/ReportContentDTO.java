package com.example.final_project_be.domain.report.dto;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportContentDTO {
    private JsonNode exerciseReport;
    private JsonNode dietReport;
    private JsonNode inbodyReport;
} 