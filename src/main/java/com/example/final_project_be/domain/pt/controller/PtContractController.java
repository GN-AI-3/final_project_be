package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.ContractMemberResponseDTO;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.pt.service.PtContractService;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PT Contract", description = "PT 계약 관련 API (트레이너 전용)")
@RequestMapping("/api/pt_contracts")
public class PtContractController {

    private final PtContractService ptContractService;

    @GetMapping("/members")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(
            summary = "계약 회원 목록 조회 (트레이너 전용)",
            description = "트레이너의 계약 회원 목록을 상태별로 조회합니다. 트레이너 권한이 필요합니다."
    )
    public ResponseEntity<List<ContractMemberResponseDTO>> getContractMembers(
            @Parameter(description = "계약 상태 (ACTIVE, COMPLETED, CANCELLED, SUSPENDED, EXPIRED)")
            @RequestParam(required = false) ContractStatus status,
            @AuthenticationPrincipal TrainerDTO trainer) {
        return ResponseEntity.ok(ptContractService.getContractMembers(trainer.getId(), status));
    }
} 