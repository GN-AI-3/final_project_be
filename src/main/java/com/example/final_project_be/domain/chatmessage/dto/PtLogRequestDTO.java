package com.example.final_project_be.domain.chatmessage.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PtLogRequestDTO {
    @NotBlank(message = "메시지는 필수입니다.")
    private String message;
    
    @NotNull(message = "PT 스케줄 ID는 필수입니다.")
    private Long ptScheduleId;
} 