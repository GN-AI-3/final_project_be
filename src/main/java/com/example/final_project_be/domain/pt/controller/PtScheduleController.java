package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.PtScheduleCancelRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleChangeRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleCreateRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleResponseDTO;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.service.PtScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pt_schedules")
@Tag(name = "PT Schedule", description = "PT 스케줄 관련 API")
public class PtScheduleController {

    private final PtScheduleService ptScheduleService;

    @GetMapping
    @Operation(summary = "PT 스케줄 목록 조회", description = "회원 또는 트레이너가 자신의 PT 스케줄을 조회합니다.")
    public ResponseEntity<List<PtScheduleResponseDTO>> getSchedules(
            @Parameter(description = "조회 시작 시간 (Unix timestamp), 기본값: 현재 시간")
            @RequestParam(required = false) Long startTime,
            @Parameter(description = "조회 종료 시간 (Unix timestamp), 기본값: 1년 뒤")
            @RequestParam(required = false) Long endTime,
            @Parameter(description = "스케줄 상태")
            @RequestParam(required = false) PtScheduleStatus status,
            @Parameter(description = "회원 ID (트레이너 ID가 제공되지 않은 경우 필수)")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "트레이너 ID (회원 ID가 제공되지 않은 경우 필수)")
            @RequestParam(required = false) Long trainerId) {

        LocalDateTime startDateTime = startTime != null ?
                Instant.ofEpochSecond(startTime).atZone(ZoneId.systemDefault()).toLocalDateTime() :
                LocalDateTime.now();
        LocalDateTime endDateTime = endTime != null ?
                Instant.ofEpochSecond(endTime).atZone(ZoneId.systemDefault()).toLocalDateTime() :
                startDateTime.plusYears(1);

        return ResponseEntity.ok(ptScheduleService.getSchedulesByDateRange(startDateTime, endDateTime, status, memberId, trainerId));
    }

    @GetMapping("/{scheduleId}")
    @Operation(summary = "PT 스케줄 상세 조회", description = "PT 스케줄의 상세 정보를 조회합니다.")
    public ResponseEntity<PtScheduleResponseDTO> getPtScheduleById(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(ptScheduleService.getPtScheduleById(scheduleId));
    }

    @PostMapping
    @Operation(summary = "PT 스케줄 등록", description = "새로운 PT 스케줄을 등록합니다.")
    public ResponseEntity<PtScheduleResponseDTO> createPtSchedule(
            @Valid @RequestBody PtScheduleCreateRequestDTO request,
            @Parameter(description = "PT 스케줄을 등록하는 회원 ID")
            @RequestParam Long memberId) {
        Long ptScheduleId = ptScheduleService.createSchedule(request, memberId, true);
        return ResponseEntity.ok(ptScheduleService.getPtScheduleById(ptScheduleId));
    }

    @PatchMapping("/{scheduleId}/cancel")
    @Operation(summary = "PT 스케줄 취소", description = "예약된 PT 스케줄을 취소합니다.")
    public ResponseEntity<PtScheduleResponseDTO> cancelPtSchedule(
            @Parameter(description = "취소할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @RequestBody(required = false) PtScheduleCancelRequestDTO request,
            @Parameter(description = "요청자 회원 ID (트레이너 ID가 제공되지 않은 경우)")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "요청자 트레이너 ID (회원 ID가 제공되지 않은 경우)")
            @RequestParam(required = false) Long trainerId) {

        String reason = request != null ? request.getReason() : null;

        Long updatedScheduleId = ptScheduleService.cancelSchedule(scheduleId, reason, memberId, trainerId);

        // 🔔 알림 직접 전송
        ptScheduleService.sendCancelAlarm(updatedScheduleId, reason);

        return ResponseEntity.ok(ptScheduleService.getPtScheduleById(updatedScheduleId));
    }

    @PatchMapping("/{scheduleId}/change")
    @Operation(summary = "PT 스케줄 변경", description = "기존 PT 스케줄을 변경하고 새로운 스케줄을 생성합니다.")
    public ResponseEntity<PtScheduleResponseDTO> changePtSchedule(
            @Parameter(description = "변경할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @Valid @RequestBody PtScheduleChangeRequestDTO request,
            @Parameter(description = "요청자 회원 ID (트레이너 ID가 제공되지 않은 경우)")
            @RequestParam(required = false) Long memberId,
            @Parameter(description = "요청자 트레이너 ID (회원 ID가 제공되지 않은 경우)")
            @RequestParam(required = false) Long trainerId) {
        Long newScheduleId = ptScheduleService.changeSchedule(scheduleId, request, memberId, trainerId);

        ptScheduleService.sendChangeAlarm(newScheduleId);

        return ResponseEntity.ok(ptScheduleService.getPtScheduleById(newScheduleId));
    }

    @PatchMapping("/{scheduleId}/no_show")
    @Operation(summary = "PT 스케줄 불참 처리", description = "트레이너가 PT 스케줄을 불참으로 처리합니다.")
    @PreAuthorize("hasRole('TRAINER')")
    public ResponseEntity<PtScheduleResponseDTO> markAsNoShow(
            @Parameter(description = "불참 처리할 PT 스케줄 ID")
            @PathVariable Long scheduleId,
            @RequestBody(required = false) PtScheduleCancelRequestDTO request,
            @Parameter(description = "불참 처리하는 트레이너 ID")
            @RequestParam Long trainerId) {

        Long updatedScheduleId = ptScheduleService.markAsNoShow(
                scheduleId,
                request != null ? request.getReason() : null,
                trainerId
        );

        return ResponseEntity.ok(ptScheduleService.getPtScheduleById(updatedScheduleId));
    }

    @GetMapping("/trainer/{trainerId}/member/schedules")
    @Operation(summary = "트레이너가 회원의 남은 PT 일정 조회", description = "트레이너가 자신의 회원 중 이름으로 검색하여 해당 회원의 남은 PT 일정을 조회합니다.")
    public ResponseEntity<List<PtScheduleResponseDTO>> getRemainingSchedulesByMemberName(
            @Parameter(description = "트레이너 ID")
            @PathVariable Long trainerId,
            @Parameter(description = "회원 이름", required = true)
            @RequestParam String memberName) {
        
        return ResponseEntity.ok(ptScheduleService.getRemainingSchedulesByTrainerIdAndMemberName(trainerId, memberName));
    }
} 