package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.PtContractResponseDTO;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.pt.service.PtContractService;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<List<PtContractResponseDTO>> getContractMembers(
            @Parameter(description = "계약 상태 (ACTIVE, COMPLETED, CANCELLED, SUSPENDED, EXPIRED)")
            @RequestParam(required = false) ContractStatus status,
            @AuthenticationPrincipal TrainerDTO trainer) {
        return ResponseEntity.ok(ptContractService.getContractMembers(trainer.getId(), status));
    }

    @GetMapping("/{ptContractId}")
    @Operation(
            summary = "PT 계약 상세 조회",
            description = "특정 PT 계약의 상세 정보를 조회합니다."
    )
    public ResponseEntity<PtContractResponseDTO> getContractMember(
            @Parameter(description = "PT 계약 ID")
            @PathVariable Long ptContractId) {
        return ResponseEntity.ok(ptContractService.getContract(ptContractId));
    }

    @PatchMapping("/{ptContractId}/status")
    @Operation(
            summary = "PT 계약 상태 변경",
            description = "PT 계약의 상태를 변경합니다. 허용된 상태 변경만 가능합니다:\n" +
                    "- ACTIVE -> SUSPENDED\n" +
                    "- ACTIVE -> CANCELLED\n" +
                    "- SUSPENDED -> ACTIVE\n" +
                    "- CANCELLED -> ACTIVE\n" +
                    "- SUSPENDED -> CANCELLED\n" +
                    "- CANCELLED -> SUSPENDED"
    )
    public ResponseEntity<PtContractResponseDTO> updateContractStatus(
            @Parameter(description = "PT 계약 ID")
            @PathVariable Long ptContractId,
            @Parameter(description = "변경할 상태 (허용된 상태 변경만 가능)", required = true)
            @RequestParam(required = true) @NotNull(message = "상태는 필수입니다.") ContractStatus status) {
        Long updatedContractId = ptContractService.updateContractStatus(ptContractId, status);
        return ResponseEntity.ok(ptContractService.getContract(updatedContractId));
    }
} 