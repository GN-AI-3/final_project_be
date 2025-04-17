package com.example.final_project_be.domain.chatmessage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtLogResponseDTO {
    private Long ptScheduleId;
    private String timestamp;
    private String finalResponse;
    private double executionTime;
} 