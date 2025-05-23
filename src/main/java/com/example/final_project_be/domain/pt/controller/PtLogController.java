package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.*;
import com.example.final_project_be.domain.pt.service.PtLogExerciseService;
import com.example.final_project_be.domain.pt.service.PtLogService;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pt_logs")
@Tag(name = "PT Log", description = "PT 로그 관련 API")
public class PtLogController {

    private final PtLogService ptLogService;
    private final PtLogExerciseService ptLogExerciseService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 생성", description = "트레이너가 PT 로그를 생성합니다.")
    public ResponseEntity<PtLogResponseDTO> createPtLog(
            @Valid @RequestBody PtLogCreateRequestDTO request,
            @AuthenticationPrincipal TrainerDTO trainer) {
        Long ptLogId = ptLogService.createPtLog(request, trainer);
        return ResponseEntity.ok(ptLogService.getPtLog(ptLogId));
    }

    @GetMapping("/{ptLogId}")
    @Operation(summary = "PT 로그 단건 조회", description = "PT 로그 ID로 단건 조회합니다.")
    public ResponseEntity<PtLogResponseDTO> getPtLog(@PathVariable Long ptLogId) {
        return ResponseEntity.ok(ptLogService.getPtLog(ptLogId));
    }

    @GetMapping
    @Operation(summary = "PT 계약별 PT 로그 조회", description = "PT 계약 ID로 PT 로그 목록을 조회합니다.")
    public ResponseEntity<List<PtLogResponseDTO>> getPtLogsByPtContractId(@RequestParam Long ptContractId) {
        return ResponseEntity.ok(ptLogService.getPtLogsByPtContractId(ptContractId));
    }

    @PatchMapping("/{ptLogId}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 수정", description = "트레이너가 PT 로그의 메타 정보를 수정합니다.")
    public ResponseEntity<PtLogResponseDTO> updatePtLog(
            @PathVariable Long ptLogId,
            @Valid @RequestBody PtLogUpdateRequestDTO request,
            @AuthenticationPrincipal TrainerDTO trainer) {
        Long updatedPtLogId = ptLogService.updatePtLog(ptLogId, request, trainer);
        return ResponseEntity.ok(ptLogService.getPtLog(updatedPtLogId));
    }

    @DeleteMapping("/{ptLogId}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 삭제", description = "트레이너가 PT 로그를 소프트 삭제합니다.")
    public ResponseEntity<Void> deletePtLog(
            @PathVariable Long ptLogId,
            @AuthenticationPrincipal TrainerDTO trainer) {
        ptLogService.deletePtLog(ptLogId, trainer);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{ptLogId}/exercises")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 운동 추가", description = "PT 로그에 운동을 추가합니다.")
    public ResponseEntity<Void> createPtLogExercise(
            @PathVariable Long ptLogId,
            @Valid @RequestBody PtLogExerciseCreateRequestDTO request
    ) {
        ptLogExerciseService.createPtLogExercise(ptLogId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ptLogId}/exercises/{exerciseLogId}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 운동 삭제", description = "PT 로그에서 운동을 삭제합니다.")
    public ResponseEntity<Void> deletePtLogExercise(
            @PathVariable Long ptLogId,
            @PathVariable Long exerciseLogId
    ) {
        ptLogExerciseService.deletePtLogExercise(ptLogId, exerciseLogId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{ptLogId}/exercises/{exerciseLogId}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 운동 수정", description = "PT 로그의 운동 정보를 수정합니다.")
    public ResponseEntity<Void> updatePtLogExercise(
            @PathVariable Long ptLogId,
            @PathVariable Long exerciseLogId,
            @Valid @RequestBody PtLogExerciseUpdateRequestDTO request
    ) {
        ptLogExerciseService.updatePtLogExercise(ptLogId, exerciseLogId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(
            summary = "회원별 PT 로그 목록 조회 (트레이너 전용)",
            description = "특정 회원의 모든 PT 로그를 조회합니다. 트레이너 권한이 필요합니다."
    )
    public ResponseEntity<List<PtLogResponseDTO>> getPtLogsByMember(
            @Parameter(description = "회원 ID", required = true)
            @PathVariable Long memberId,
            @AuthenticationPrincipal TrainerDTO trainer) {
        List<PtLogResponseDTO> ptLogs = ptLogService.getPtLogsByMemberIdForTrainer(memberId, trainer.getId());
        return ResponseEntity.ok(ptLogs);
    }

} 