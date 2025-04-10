package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractMemberResponseDTO {
    private Long memberId;
    private String memberName;
    private String phone;
    private ContractSummaryDTO contract;

    public static ContractMemberResponseDTO from(PtContract contract) {
        return ContractMemberResponseDTO.builder()
                .memberId(contract.getMember().getId())
                .memberName(contract.getMember().getName())
                .phone(contract.getMember().getPhone())
                .contract(ContractSummaryDTO.builder()
                        .contractId(contract.getId())
                        .totalCount(contract.getTotalCount())
                        .remainingCount(contract.getRemainingCount())
                        .status(contract.getStatus())
                        .build())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContractSummaryDTO {
        private Long contractId;
        private Integer totalCount;
        private Integer remainingCount;
        private ContractStatus status;
    }
} 