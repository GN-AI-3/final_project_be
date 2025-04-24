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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    /**
     * PT 계약 ID로 회원 정보를 조회합니다.
     * 트레이너 권한이 있는지 검증합니다.
     *
     * @param contractId PT 계약 ID
     * @param trainerId 트레이너 ID
     * @return 회원 정보 (회원 ID 포함)
     * @throws IllegalArgumentException 계약을 찾을 수 없거나 트레이너 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getContractMemberInfo(Long contractId, Long trainerId) {
        PtContract contract = ptContractRepository.findByIdWithMemberAndTrainer(contractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));
        
        // 트레이너 권한 검증
        if (!contract.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("해당 PT 계약에 접근할 권한이 없습니다.");
        }
        
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("ptContractId", contract.getId());
        memberInfo.put("memberId", contract.getMember().getId());
        memberInfo.put("memberName", contract.getMember().getName());
        memberInfo.put("memberEmail", contract.getMember().getEmail());
        memberInfo.put("status", contract.getStatus());
        memberInfo.put("startDate", contract.getStartDate());
        memberInfo.put("endDate", contract.getEndDate());
        memberInfo.put("totalCount", contract.getTotalCount());
        memberInfo.put("usedCount", contract.getUsedCount());
        
        return memberInfo;
    }

    /**
     * PT 계약 ID와 회원 ID로 트레이너와 회원 관계를 검증합니다.
     * 해당 PT 계약이 트레이너의 계약인지, 회원이 해당 계약에 속하는지 검증합니다.
     *
     * @param ptContractId PT 계약 ID
     * @param memberId     회원 ID
     * @param trainerId    트레이너 ID
     * @throws IllegalArgumentException 계약이 없거나, 트레이너 권한이 없거나, 회원이 계약에 속하지 않는 경우
     */
    @Transactional(readOnly = true)
    public void validateTrainerMemberRelationship(Long ptContractId, Long memberId, Long trainerId) {
        log.info("트레이너-회원 관계 검증 - PT 계약 ID: {}, 회원 ID: {}, 트레이너 ID: {}", 
                ptContractId, memberId, trainerId);
        
        PtContract contract = ptContractRepository.findByIdWithMemberAndTrainer(ptContractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));
        
        // 트레이너 권한 검증
        if (!contract.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("해당 PT 계약에 접근할 권한이 없습니다.");
        }
        
        // 회원 소속 검증
        if (!contract.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 회원은 이 PT 계약에 속하지 않습니다.");
        }
    }
    
    /**
     * 회원 ID와 트레이너 ID로 접근 권한을 검증합니다.
     * 트레이너가 해당 회원의 계약을 담당하고 있는지 확인합니다.
     *
     * @param memberId 회원 ID
     * @param trainerId 트레이너 ID
     * @throws IllegalArgumentException 트레이너가 해당 회원의 계약을 담당하고 있지 않은 경우
     */
    @Transactional(readOnly = true)
    public void validateTrainerHasAccessToMember(Long memberId, Long trainerId) {
        log.info("트레이너의 회원 접근 권한 검증 - 회원 ID: {}, 트레이너 ID: {}", memberId, trainerId);
        
        // 회원과 트레이너 간의 계약이 존재하는지 확인
        boolean hasContract = ptContractRepository.existsByMemberIdAndTrainerId(memberId, trainerId);
        if (!hasContract) {
            throw new IllegalArgumentException("해당 회원과의 계약이 존재하지 않습니다.");
        }
    }
} 