package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.pt.dto.PtContractResponseDTO;
import com.example.final_project_be.domain.pt.dto.PtContractUpdateRequestDTO;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
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

    @Transactional
    public Long updateContract(Long contractId, PtContractUpdateRequestDTO updateRequest) {
        PtContract contract = ptContractRepository.findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));

        // 수정 가능한 필드 업데이트
        if (updateRequest.getEndDate() != null) {
            LocalDateTime endDateTime = Instant.ofEpochSecond(updateRequest.getEndDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            contract.setEndDate(endDateTime);
        }
        if (updateRequest.getMemo() != null) {
            contract.setMemo(updateRequest.getMemo());
        }
        if (updateRequest.getTotalCount() != null) {
            // 총 PT 횟수는 사용된 횟수보다 커야 함
            if (updateRequest.getTotalCount() < contract.getUsedCount()) {
                throw new IllegalArgumentException("총 PT 횟수는 사용된 횟수보다 커야 합니다.");
            }
            contract.setTotalCount(updateRequest.getTotalCount());
        }

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
     * - SUSPENDED -> CANCELLED
     * - CANCELLED -> SUSPENDED
     */
    private boolean isValidStatusTransition(ContractStatus currentStatus, ContractStatus newStatus) {
        return switch (currentStatus) {
            case ACTIVE -> newStatus == ContractStatus.SUSPENDED || newStatus == ContractStatus.CANCELLED;
            case SUSPENDED -> newStatus == ContractStatus.ACTIVE || newStatus == ContractStatus.CANCELLED;
            case CANCELLED -> newStatus == ContractStatus.ACTIVE || newStatus == ContractStatus.SUSPENDED;
            default -> false;
        };
    }
} 