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
public class PtScheduleMemberResponseDTO {
    private Long id;
    private Long ptContractId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private PtScheduleStatus status;
    private String reservationId;
    private Long trainerId;
    private String trainerName;
    private Integer currentPtCount;
    private Integer totalCount;

    public static PtScheduleMemberResponseDTO from(PtSchedule ptSchedule) {
        return PtScheduleMemberResponseDTO.builder()
                .id(ptSchedule.getId())
                .ptContractId(ptSchedule.getPtContract().getId())
                .startTime(ptSchedule.getStartTime())
                .endTime(ptSchedule.getEndTime())
                .status(ptSchedule.getStatus())
                .reservationId(ptSchedule.getReservationId())
                .trainerId(ptSchedule.getPtContract().getTrainer().getId())
                .trainerName(ptSchedule.getPtContract().getTrainer().getName())
                .currentPtCount(ptSchedule.getCurrentPtCount())
                .totalCount(ptSchedule.getPtContract().getTotalCount())
                .build();
    }
} 