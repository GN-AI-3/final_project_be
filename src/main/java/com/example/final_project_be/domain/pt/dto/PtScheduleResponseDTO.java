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
public class PtScheduleResponseDTO {
    private Long id;
    private Long ptContractId;
    private Long startTime;
    private Long endTime;
    private PtScheduleStatus status;
    private String reason;
    private String reservationId;
    private Long trainerId;
    private String trainerName;
    private Long memberId;
    private String memberName;
    private Integer currentPtCount;
    private Integer totalCount;
    private Integer remainingPtCount;

    public static PtScheduleResponseDTO from(PtSchedule ptSchedule) {
        return PtScheduleResponseDTO.builder()
                .id(ptSchedule.getId())
                .ptContractId(ptSchedule.getPtContract().getId())
                .startTime(ptSchedule.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                .endTime(ptSchedule.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                .status(ptSchedule.getStatus())
                .reason(ptSchedule.getReason())
                .reservationId(ptSchedule.getReservationId())
                .trainerId(ptSchedule.getPtContract().getTrainer().getId())
                .trainerName(ptSchedule.getPtContract().getTrainer().getName())
                .memberId(ptSchedule.getPtContract().getMember().getId())
                .memberName(ptSchedule.getPtContract().getMember().getName())
                .currentPtCount(ptSchedule.getCurrentPtCount())
                .totalCount(ptSchedule.getPtContract().getTotalCount())
                .remainingPtCount(ptSchedule.getPtContract().getTotalCount() - ptSchedule.getPtContract().getUsedCount())
                .build();
    }
} 