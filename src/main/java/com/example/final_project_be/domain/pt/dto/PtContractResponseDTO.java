package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZoneOffset;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtContractResponseDTO {
    private Long id;
    private Long createdAt;
    private Long modifiedAt;
    private Long startDate;
    private Long endDate;
    private String memo;
    private ContractStatus status;
    private Integer totalCount;
    private Integer usedCount;
    private Integer remainingCount;
    private Long memberId;
    private String memberName;
    private String gender;
    private String email;
    private String phone;
    private Long trainerId;
    private String trainerName;
    private Long createdBy;
    private Long modifiedBy;

    public static PtContractResponseDTO from(PtContract contract) {
        return PtContractResponseDTO.builder()
                .id(contract.getId())
                .createdAt(contract.getCreatedAt().atOffset(ZoneOffset.UTC).toEpochSecond())
                .modifiedAt(contract.getModifiedAt().atOffset(ZoneOffset.UTC).toEpochSecond())
                .startDate(contract.getStartDate().atOffset(ZoneOffset.UTC).toEpochSecond())
                .endDate(contract.getEndDate().atOffset(ZoneOffset.UTC).toEpochSecond())
                .memo(contract.getMemo())
                .status(contract.getStatus())
                .totalCount(contract.getTotalCount())
                .usedCount(contract.getUsedCount())
                .remainingCount(contract.getRemainingCount())
                .memberId(contract.getMember().getId())
                .memberName(contract.getMember().getName())
                .gender(contract.getMember().getGender())
                .email(contract.getMember().getEmail())
                .phone(contract.getMember().getPhone())
                .trainerId(contract.getTrainer().getId())
                .trainerName(contract.getTrainer().getName())
                .createdBy(contract.getCreatedBy())
                .modifiedBy(contract.getModifiedBy())
                .build();
    }
} 