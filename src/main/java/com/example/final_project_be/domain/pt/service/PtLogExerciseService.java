package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.pt.dto.request.CreatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.dto.request.UpdatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import com.example.final_project_be.domain.pt.repository.PtLogExerciseRepository;
import com.example.final_project_be.domain.pt.repository.PtLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtLogExerciseService {

    private final PtLogRepository ptLogRepository;
    private final PtLogExerciseRepository ptLogExerciseRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public void createPtLogExercise(Long ptLogId, CreatePtLogExerciseRequest request) {
        PtLog ptLog = ptLogRepository.findById(ptLogId)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));

        PtLogExercise ptLogExercise = PtLogExercise.builder()
                .ptLogs(ptLog)
                .exercise(exercise)
                .sequence(request.getSequence())
                .sets(request.getSets())
                .reps(request.getReps())
                .weight(request.getWeight())
                .restTime(request.getRestTime())
                .feedback(request.getFeedback())
                .build();

        ptLogExerciseRepository.save(ptLogExercise);
    }

    @Transactional
    public void deletePtLogExercise(Long ptLogId, Long exerciseLogId) {
        PtLog ptLog = ptLogRepository.findById(ptLogId)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        PtLogExercise ptLogExercise = ptLogExerciseRepository.findById(exerciseLogId)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그 운동을 찾을 수 없습니다."));

        if (!ptLogExercise.getPtLogs().getId().equals(ptLogId)) {
            throw new IllegalArgumentException("PT 로그 운동을 찾을 수 없습니다.");
        }

        ptLogExerciseRepository.delete(ptLogExercise);
    }

    @Transactional
    public void updatePtLogExercise(Long ptLogId, Long exerciseLogId, UpdatePtLogExerciseRequest request) {
        PtLog ptLog = ptLogRepository.findById(ptLogId)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        PtLogExercise ptLogExercise = ptLogExerciseRepository.findById(exerciseLogId)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그 운동을 찾을 수 없습니다."));

        if (!ptLogExercise.getPtLogs().getId().equals(ptLogId)) {
            throw new IllegalArgumentException("PT 로그 운동을 찾을 수 없습니다.");
        }

        if (request.getSequence() != null) {
            ptLogExercise.setSequence(request.getSequence());
        }
        if (request.getSets() != null) {
            ptLogExercise.setSets(request.getSets());
        }
        if (request.getReps() != null) {
            ptLogExercise.setReps(request.getReps());
        }
        if (request.getWeight() != null) {
            ptLogExercise.setWeight(request.getWeight());
        }
        if (request.getRestTime() != null) {
            ptLogExercise.setRestTime(request.getRestTime());
        }
        if (request.getFeedback() != null) {
            ptLogExercise.setFeedback(request.getFeedback());
        }
    }
} 