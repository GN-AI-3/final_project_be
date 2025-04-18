package com.example.final_project_be.domain.trainer.controller;

import com.example.final_project_be.domain.trainer.dto.*;
import com.example.final_project_be.domain.trainer.enums.SessionDuration;
import com.example.final_project_be.domain.trainer.service.TrainerScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Tag(name = "트레이너 스케줄 관리", description = "트레이너의 스케줄, 근무시간, 불가능 시간 등을 관리하는 API")
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerScheduleController {

    private final TrainerScheduleService trainerScheduleService;

    @Operation(summary = "트레이너 근무 시간 수정", description = "트레이너의 요일별 근무 시간을 수정합니다.")
    @PutMapping("/{trainerId}/working-times")
    public ResponseEntity<TrainerWorkingTimeUpdateResponseDTO> updateWorkingTime(
            @Parameter(description = "트레이너 ID") @PathVariable Long trainerId,
            @RequestBody @Valid List<TrainerWorkingTimeUpdateRequestDTO> requests) {
        TrainerWorkingTimeUpdateResponseDTO response = trainerScheduleService.updateWorkingTime(trainerId, requests);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "트레이너 불가능 시간 등록", description = "트레이너의 특정 시간대 불가능 시간을 등록합니다.")
    @PostMapping("/{trainerId}/unavailable-times")
    public ResponseEntity<TrainerUnavailableTimeResponseDTO> createUnavailableTime(
            @Parameter(description = "트레이너 ID") @PathVariable Long trainerId,
            @RequestBody @Valid TrainerUnavailableTimeCreateRequestDTO request) {
        TrainerUnavailableTimeResponseDTO response = trainerScheduleService.createUnavailableTime(trainerId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "트레이너 가용 시간 조회", description = "트레이너의 특정 기간 동안의 가용 시간을 조회합니다.")
    @GetMapping("/{trainerId}/available-times")
    public ResponseEntity<TrainerAvailableTimesResponseDTO> getAvailableTimes(
            @Parameter(description = "트레이너 ID") @PathVariable Long trainerId,
            @Parameter(description = "조회 시작 시간 (Unix timestamp 초), 미입력시 현재 시간") @RequestParam(required = false) Long startTime,
            @Parameter(description = "조회 종료 시간 (Unix timestamp 초), 미입력시 현재 시간 + 1주일") @RequestParam(required = false) Long endTime,
            @Parameter(description = "세션 시간 (30분, 60분, 90분 중 선택), 미입력시 60분") @RequestParam(required = false) SessionDuration sessionDuration
    ) {
        // 기본값 설정
        long currentTime = Instant.now().getEpochSecond();
        long defaultStartTime = startTime != null ? startTime : currentTime;
        long defaultEndTime = endTime != null ? endTime : defaultStartTime + (7 * 24 * 60 * 60); // 1주일
        SessionDuration defaultSessionDuration = sessionDuration != null ? sessionDuration : SessionDuration.SIXTY_MINUTES;

        // Unix timestamp를 LocalDateTime으로 변환
        LocalDateTime startDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(defaultStartTime), ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(defaultEndTime), ZoneId.systemDefault());

        return ResponseEntity.ok(trainerScheduleService.getAvailableTimes(
                trainerId,
                startDateTime,
                endDateTime,
                defaultSessionDuration.getMinutes()
        ));
    }
} 