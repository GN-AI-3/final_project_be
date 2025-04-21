package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrainerWorkingTimeCreateRequestDTO {
    @NotNull(message = "요일은 필수입니다.")
    private DayOfWeek day;

    @NotNull(message = "시작 시간은 필수입니다.")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "시작 시간은 HH:mm 형식이어야 합니다.")
    private String startTime;

    @NotNull(message = "종료 시간은 필수입니다.")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "종료 시간은 HH:mm 형식이어야 합니다.")
    private String endTime;

    @NotNull(message = "활성화 여부는 필수입니다.")
    private boolean isActive;
} 