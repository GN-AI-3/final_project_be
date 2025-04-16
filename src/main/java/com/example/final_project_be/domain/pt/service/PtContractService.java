package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.pt.dto.PtContractResponseDTO;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtContractService {

    private final PtContractRepository ptContractRepository;

    public List<PtContractResponseDTO> getContractMembers(Long trainerId, ContractStatus status) {
        List<PtContract> contracts = (status != null)
                ? ptContractRepository.findByTrainerIdAndStatus(trainerId, status)
                : ptContractRepository.findByTrainerId(trainerId);

        return contracts.stream()
                .map(PtContractResponseDTO::from)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public PtContractResponseDTO getContract(Long contractId) {
        PtContract contract = ptContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));
        return PtContractResponseDTO.from(contract);
    }

    @Transactional
    public Long updateContractStatus(Long contractId, ContractStatus status) {
        PtContract contract = ptContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));

        // 현재 상태와 변경하려는 상태에 따른 유효성 검사
        ContractStatus currentStatus = contract.getStatus();
        if (!isValidStatusTransition(currentStatus, status)) {
            throw new IllegalArgumentException("허용되지 않은 상태 변경입니다. 현재 상태: " + currentStatus + ", 변경하려는 상태: " + status);
        }

        contract.setStatus(status);
        ptContractRepository.save(contract);
        return contract.getId();
    }

    /**
     * PT 계약 상태 변경의 유효성을 검사합니다.
     * 허용된 상태 변경:
     * - ACTIVE -> SUSPENDED
     * - ACTIVE -> CANCELLED
     * - SUSPENDED -> ACTIVE
     * - CANCELLED -> ACTIVE
     */
    private boolean isValidStatusTransition(ContractStatus currentStatus, ContractStatus newStatus) {
        return switch (currentStatus) {
            case ACTIVE -> newStatus == ContractStatus.SUSPENDED || newStatus == ContractStatus.CANCELLED;
            case SUSPENDED, CANCELLED -> newStatus == ContractStatus.ACTIVE;
            default -> false;
        };
    }
} 