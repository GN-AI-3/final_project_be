package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneId;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtScheduleTrainerResponseDTO {
    private Long id;
    private Long ptContractId;
    private Long startTime;
    private Long endTime;
    private PtScheduleStatus status;
    private String reservationId;
    private Long trainerId;
    private Long memberId;
    private String memberName;
    private Integer remainingPtCount;

    public static PtScheduleTrainerResponseDTO from(PtSchedule ptSchedule) {
        return PtScheduleTrainerResponseDTO.builder()
                .id(ptSchedule.getId())
                .ptContractId(ptSchedule.getPtContract().getId())
                .startTime(ptSchedule.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                .endTime(ptSchedule.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                .status(ptSchedule.getStatus())
                .reservationId(ptSchedule.getReservationId())
                .trainerId(ptSchedule.getPtContract().getTrainer().getId())
                .memberId(ptSchedule.getPtContract().getMember().getId())
                .memberName(ptSchedule.getPtContract().getMember().getName())
                .remainingPtCount(ptSchedule.getPtContract().getRemainingCount())
                .build();
    }
} 