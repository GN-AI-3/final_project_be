package com.example.final_project_be.domain.trainer.controller;

import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateResponseDTO;
import com.example.final_project_be.domain.trainer.service.TrainerWorkingTimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
public class TrainerWorkingTimeController {

    private final TrainerWorkingTimeService trainerWorkingTimeService;

    @PutMapping("/{trainerId}/working-time")
    public ResponseEntity<TrainerWorkingTimeUpdateResponseDTO> updateWorkingTime(
            @PathVariable Long trainerId,
            @RequestBody @Valid List<TrainerWorkingTimeUpdateRequestDTO> requests) {
        TrainerWorkingTimeUpdateResponseDTO response = trainerWorkingTimeService.updateWorkingTime(trainerId, requests);
        return ResponseEntity.ok(response);
    }
} 