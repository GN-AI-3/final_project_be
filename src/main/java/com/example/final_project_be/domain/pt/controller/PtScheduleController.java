package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.*;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.service.PtScheduleService;
import com.example.final_project_be.security.MemberDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
                startDateTime.plusYears(1);

        return ResponseEntity.ok(ptScheduleService.getSchedulesByDateRange(startDateTime, endDateTime, status, user));
    }

    @PostMapping("/api/pt_schedules")
    @Operation(summary = "PT 스케줄 등록", description = "새로운 PT 스케줄을 등록합니다.")
    public ResponseEntity<PtScheduleResponseDTO> createPtSchedule(
            @Valid @RequestBody PtScheduleCreateRequestDTO request,
            @AuthenticationPrincipal MemberDTO member) {
        Long ptScheduleId = ptScheduleService.createSchedule(request, member, true);
        PtSchedule ptSchedule = ptScheduleService.getPtSchedule(ptScheduleId);
        return ResponseEntity.ok(PtScheduleResponseDTO.from(ptSchedule));
    }

    @PatchMapping("/api/pt_schedules/{scheduleId}/cancel")
    @Operation(summary = "PT 스케줄 취소", description = "예약된 PT 스케줄을 취소합니다.")
    public ResponseEntity<PtScheduleResponseDTO> cancelPtSchedule(
            @Parameter(description = "취소할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @RequestBody(required = false) PtScheduleCancelRequestDTO request,
            @AuthenticationPrincipal Object user) {

        Long updatedScheduleId = ptScheduleService.cancelSchedule(
                scheduleId,
                request != null ? request.getReason() : null,
                user
        );

        PtSchedule ptSchedule = ptScheduleService.getPtSchedule(updatedScheduleId);
        return ResponseEntity.ok(PtScheduleResponseDTO.from(ptSchedule));
    }

    @PatchMapping("/api/pt_schedules/{scheduleId}/change")
    @Operation(summary = "PT 스케줄 변경", description = "기존 PT 스케줄을 변경하고 새로운 스케줄을 생성합니다.")
    public ResponseEntity<PtScheduleChangeResponseDTO> changePtSchedule(
            @Parameter(description = "변경할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @Valid @RequestBody PtScheduleChangeRequestDTO request,
            @AuthenticationPrincipal Object user) {
        return ResponseEntity.ok(ptScheduleService.changeSchedule(scheduleId, request, user));
    }

    @PatchMapping("/api/pt_schedules/{scheduleId}/no_show")
    @Operation(summary = "PT 스케줄 불참 처리", description = "트레이너가 PT 스케줄을 불참으로 처리합니다.")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<PtScheduleResponseDTO> markAsNoShow(
            @Parameter(description = "불참 처리할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @RequestBody(required = false) PtScheduleCancelRequestDTO request,
            @AuthenticationPrincipal Object user) {

        PtSchedule ptSchedule = ptScheduleService.markAsNoShow(
                scheduleId,
                request != null ? request.getReason() : null,
                user
        );

        return ResponseEntity.ok(PtScheduleResponseDTO.from(ptSchedule));
    }
} 