package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.PtLogCreateRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtLogResponseDTO;
import com.example.final_project_be.domain.pt.service.PtLogService;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pt_logs")
@Tag(name = "PT Log", description = "PT 로그 관련 API")
public class PtLogController {

    private final PtLogService ptLogService;

    @PostMapping
    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 로그 생성", description = "트레이너가 PT 로그를 생성합니다.")
    public ResponseEntity<PtLogResponseDTO> createPtLog(
            @Valid @RequestBody PtLogCreateRequestDTO request,
            @AuthenticationPrincipal TrainerDTO trainer) {
        Long ptLogId = ptLogService.createPtLog(request, trainer);
        return ResponseEntity.ok(ptLogService.getPtLog(ptLogId));
    }
} 