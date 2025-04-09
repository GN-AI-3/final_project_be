package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.PtScheduleResponseDTO;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.service.PtScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PT Schedule", description = "PT 스케줄 관련 API")
public class PtScheduleController {

    private final PtScheduleService ptScheduleService;

    @GetMapping("/api/pt_schedules")
    @Operation(summary = "PT 스케줄 조회", description = "회원 또는 트레이너가 자신의 PT 스케줄을 조회합니다.")
    public ResponseEntity<List<PtScheduleResponseDTO>> getSchedules(
            @Parameter(description = "조회 시작 시간 (Unix timestamp), 기본값: 현재 시간")
            @RequestParam(required = false) Long startTime,
            @Parameter(description = "조회 종료 시간 (Unix timestamp), 기본값: 1년 뒤")
            @RequestParam(required = false) Long endTime,
            @Parameter(description = "스케줄 상태")
            @RequestParam(required = false) PtScheduleStatus status,
            @AuthenticationPrincipal Object user) {

        LocalDateTime startDateTime = startTime != null ?
                Instant.ofEpochSecond(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now();
        LocalDateTime endDateTime = endTime != null ?
                Instant.ofEpochSecond(endTime).atZone(ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now().plusYears(1);

        return ResponseEntity.ok(ptScheduleService.getSchedulesByDateRange(startDateTime, endDateTime, status, user));
    }
} 