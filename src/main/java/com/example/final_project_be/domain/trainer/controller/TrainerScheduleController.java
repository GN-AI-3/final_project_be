package com.example.final_project_be.domain.trainer.controller;

import com.example.final_project_be.domain.trainer.dto.TrainerUnavailableTimeCreateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerUnavailableTimeResponseDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateResponseDTO;
import com.example.final_project_be.domain.trainer.service.TrainerScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerScheduleController {

    private final TrainerScheduleService trainerScheduleService;

    @PutMapping("/{trainerId}/working-times")
    public ResponseEntity<TrainerWorkingTimeUpdateResponseDTO> updateWorkingTime(
            @PathVariable Long trainerId,
            @RequestBody @Valid List<TrainerWorkingTimeUpdateRequestDTO> requests) {
        TrainerWorkingTimeUpdateResponseDTO response = trainerScheduleService.updateWorkingTime(trainerId, requests);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{trainerId}/unavailable-times")
    public ResponseEntity<TrainerUnavailableTimeResponseDTO> createUnavailableTime(
            @PathVariable Long trainerId,
            @RequestBody @Valid TrainerUnavailableTimeCreateRequestDTO request) {
        TrainerUnavailableTimeResponseDTO response = trainerScheduleService.createUnavailableTime(trainerId, request);
        return ResponseEntity.ok(response);
    }
} 