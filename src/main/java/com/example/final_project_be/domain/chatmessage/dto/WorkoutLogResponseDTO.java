package com.example.final_project_be.domain.chatmessage.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkoutLogResponseDTO {
    private Long memberId;
    private String timestamp;
    private String finalResponse;
    private double executionTime;
} 