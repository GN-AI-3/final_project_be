package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.pt.dto.PtLogCreateRequestDTO;
import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.repository.PtLogRepository;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.security.TrainerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PtLogService {

    private final PtLogRepository ptLogRepository;
    private final PtScheduleRepository ptScheduleRepository;
    private final ExerciseRepository exerciseRepository;

    public Long createPtLog(PtLogCreateRequestDTO request, TrainerDTO trainer) {
        // PT 스케줄 조회
        PtSchedule ptSchedule = ptScheduleRepository.findByIdWithContractAndMembers(request.getPtScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다."));

        // PT 로그 생성
        PtLog ptLog = PtLog.builder()
                .ptSchedule(ptSchedule)
                .member(ptSchedule.getPtContract().getMember())
                .feedback(request.getFeedback())
                .injuryCheck(request.isInjuryCheck())
                .nextPlan(request.getNextPlan())
                .created_by(trainer.getId())
                .build();

        // PT 로그 저장
        PtLog savedPtLog = ptLogRepository.save(ptLog);

        // 운동 로그 생성
        List<PtLogExercise> exercises = IntStream.range(0, request.getExercises().size())
                .mapToObj(i -> {
                    PtLogCreateRequestDTO.ExerciseLogDTO exerciseDto = request.getExercises().get(i);
                    Exercise exercise = exerciseRepository.findById(exerciseDto.getExerciseId())
                            .orElseThrow(() -> new IllegalArgumentException("운동을 찾을 수 없습니다."));

                    return PtLogExercise.builder()
                            .ptLogs(savedPtLog)
                            .exercise(exercise)
                            .sequence(i + 1)
                            .sets(exerciseDto.getSets())
                            .reps(exerciseDto.getReps())
                            .weight(exerciseDto.getWeight())
                            .restTime(exerciseDto.getRestTime())
                            .correctionNote(exerciseDto.getCorrectionNote())
                            .feedback(exerciseDto.getFeedback())
                            .created_by(trainer.getId())
                            .build();
                })
                .toList();

        // 운동 로그 저장
        ptLogRepository.saveAll(exercises);

        return savedPtLog.getId();
    }
} 