package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtScheduleChangeResponseDTO {
    private PtScheduleResponseDTO oldSchedule;
    private PtScheduleResponseDTO newSchedule;
    private String message;

    public static PtScheduleChangeResponseDTO of(PtSchedule oldSchedule, PtSchedule newSchedule) {
        return PtScheduleChangeResponseDTO.builder()
                .oldSchedule(PtScheduleResponseDTO.from(oldSchedule))
                .newSchedule(PtScheduleResponseDTO.from(newSchedule))
                .message("PT 일정이 성공적으로 변경되었습니다.")
                .build();
    }
} 