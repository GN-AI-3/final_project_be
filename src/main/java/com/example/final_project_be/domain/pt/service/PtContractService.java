package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.pt.dto.ContractMemberResponseDTO;
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

    public List<ContractMemberResponseDTO> getContractMembers(Long trainerId, ContractStatus status) {
        List<PtContract> contracts = (status != null)
                ? ptContractRepository.findByTrainerIdAndStatus(trainerId, status)
                : ptContractRepository.findByTrainerId(trainerId);

        return contracts.stream()
                .map(ContractMemberResponseDTO::from)
                .collect(Collectors.toList());
    }
} 