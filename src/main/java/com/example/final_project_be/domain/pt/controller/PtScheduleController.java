package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.PtScheduleMemberResponseDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleTrainerResponseDTO;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.service.PtScheduleService;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "PT Schedule", description = "PT 스케줄 관련 API")
public class PtScheduleController {

    private final PtScheduleService ptScheduleService;

    @GetMapping("/api/trainer/pt_schedules")
    @Operation(summary = "트레이너용 PT 스케줄 조회", description = "트레이너가 자신의 PT 스케줄을 조회합니다.")
    public ResponseEntity<List<PtScheduleTrainerResponseDTO>> getTrainerSchedules(
            @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "스케줄 상태") @RequestParam(required = false) PtScheduleStatus status,
            @AuthenticationPrincipal TrainerDTO trainer) {

        return ResponseEntity.ok(ptScheduleService.getSchedulesByDateRange(startDate, endDate, status, trainer));
    }

    @GetMapping("/api/member/pt_schedules")
    @Operation(summary = "회원용 PT 스케줄 조회", description = "회원이 자신의 PT 스케줄을 조회합니다.")
    public ResponseEntity<List<PtScheduleMemberResponseDTO>> getMemberSchedules(
            @Parameter(description = "조회 시작 날짜 (yyyy-MM-dd)", required = true) @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @Parameter(description = "조회 종료 날짜 (yyyy-MM-dd)") @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @Parameter(description = "스케줄 상태") @RequestParam(required = false) PtScheduleStatus status,
            @AuthenticationPrincipal MemberDTO member) {

        return ResponseEntity.ok(ptScheduleService.getSchedulesByDateRange(startDate, endDate, status, member));
    }
} 