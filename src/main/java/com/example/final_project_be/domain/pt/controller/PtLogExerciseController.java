package com.example.final_project_be.domain.pt.controller;

import com.example.final_project_be.domain.pt.dto.request.CreatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.dto.request.UpdatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.service.PtLogExerciseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pt-logs")
@RequiredArgsConstructor
public class PtLogExerciseController {

    private final PtLogExerciseService ptLogExerciseService;

    @PostMapping("/{ptLogId}/exercises")
    public ResponseEntity<Void> createPtLogExercise(
            @PathVariable Long ptLogId,
            @Valid @RequestBody CreatePtLogExerciseRequest request
    ) {
        ptLogExerciseService.createPtLogExercise(ptLogId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ptLogId}/exercises/{exerciseLogId}")
    public ResponseEntity<Void> deletePtLogExercise(
            @PathVariable Long ptLogId,
            @PathVariable Long exerciseLogId
    ) {
        ptLogExerciseService.deletePtLogExercise(ptLogId, exerciseLogId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{ptLogId}/exercises/{exerciseLogId}")
    public ResponseEntity<Void> updatePtLogExercise(
            @PathVariable Long ptLogId,
            @PathVariable Long exerciseLogId,
            @Valid @RequestBody UpdatePtLogExerciseRequest request
    ) {
        ptLogExerciseService.updatePtLogExercise(ptLogId, exerciseLogId, request);
        return ResponseEntity.ok().build();
    }
} 