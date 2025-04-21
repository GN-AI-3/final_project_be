package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrainerWorkingTimeUpdateRequestDTO {
    @NotNull(message = "요일은 필수입니다")
    private DayOfWeek day;

    @NotNull(message = "시작 시간은 필수입니다")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "시작 시간 형식이 올바르지 않습니다 (HH:mm)")
    private String startTime;

    @NotNull(message = "종료 시간은 필수입니다")
    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$", message = "종료 시간 형식이 올바르지 않습니다 (HH:mm)")
    private String endTime;

    @NotNull(message = "활성화 여부는 필수입니다")
    private Boolean isActive = true;
} 