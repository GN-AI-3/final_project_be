package com.example.final_project_be.domain.pt.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.final_project_be.domain.pt.dto.PtLogExerciseResponseDTO;
import com.example.final_project_be.domain.pt.service.PtLogExerciseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/pt-log-exercises")
@RequiredArgsConstructor
@Tag(name = "pt-log-exercise", description = "PT 로그 운동 관련 API")
public class PtLogExerciseController {

    private final PtLogExerciseService ptLogExerciseService;

    @GetMapping("/pt-schedule/{ptScheduleId}")
    @Operation(summary = "PT 스케줄 ID로 운동 목록 조회", 
              description = "PT 스케줄 ID를 통해 해당 PT 로그의 모든 운동 목록을 조회합니다.")
    public ResponseEntity<List<PtLogExerciseResponseDTO>> getExercisesByPtScheduleId(
            @PathVariable Long ptScheduleId) {
        List<PtLogExerciseResponseDTO> exercises = ptLogExerciseService.getExercisesByPtScheduleId(ptScheduleId);
        return ResponseEntity.ok(exercises);
    }
} 