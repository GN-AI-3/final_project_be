package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.pt.dto.request.CreatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.dto.request.UpdatePtLogExerciseRequest;
import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import com.example.final_project_be.domain.pt.repository.PtLogExerciseRepository;
import com.example.final_project_be.domain.pt.repository.PtLogRepository;
import com.example.final_project_be.global.error.exception.BusinessException;
import com.example.final_project_be.global.error.exception.ErrorCode;
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
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_LOG_NOT_FOUND));

        Exercise exercise = exerciseRepository.findById(request.getExerciseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.EXERCISE_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_LOG_NOT_FOUND));

        PtLogExercise ptLogExercise = ptLogExerciseRepository.findById(exerciseLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_LOG_EXERCISE_NOT_FOUND));

        if (!ptLogExercise.getPtLogs().getId().equals(ptLogId)) {
            throw new BusinessException(ErrorCode.PT_LOG_EXERCISE_NOT_FOUND);
        }

        ptLogExerciseRepository.delete(ptLogExercise);
    }

    @Transactional
    public void updatePtLogExercise(Long ptLogId, Long exerciseLogId, UpdatePtLogExerciseRequest request) {
        PtLog ptLog = ptLogRepository.findById(ptLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_LOG_NOT_FOUND));

        PtLogExercise ptLogExercise = ptLogExerciseRepository.findById(exerciseLogId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PT_LOG_EXERCISE_NOT_FOUND));

        if (!ptLogExercise.getPtLogs().getId().equals(ptLogId)) {
            throw new BusinessException(ErrorCode.PT_LOG_EXERCISE_NOT_FOUND);
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