package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtScheduleTrainerResponseDTO {
    private Long id;
    private Long ptContractId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PtScheduleStatus status;
    private String reservationId;
    private Long memberId;
    private String memberName;
    private Integer remainingPtCount;

    public static PtScheduleTrainerResponseDTO from(PtSchedule ptSchedule) {
        return PtScheduleTrainerResponseDTO.builder()
                .id(ptSchedule.getId())
                .ptContractId(ptSchedule.getPtContract().getId())
                .startTime(ptSchedule.getStartTime())
                .endTime(ptSchedule.getEndTime())
                .status(ptSchedule.getStatus())
                .reservationId(ptSchedule.getReservationId())
                .memberId(ptSchedule.getPtContract().getMember().getId())
                .memberName(ptSchedule.getPtContract().getMember().getName())
                .remainingPtCount(ptSchedule.getPtContract().getRemainingCount())
                .build();
    }
} 