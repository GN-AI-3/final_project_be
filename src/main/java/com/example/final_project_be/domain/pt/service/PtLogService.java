package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.pt.dto.PtLogCreateRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtLogResponseDTO;
import com.example.final_project_be.domain.pt.dto.PtLogUpdateRequestDTO;
import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.repository.PtLogExerciseRepository;
import com.example.final_project_be.domain.pt.repository.PtLogRepository;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.security.TrainerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class PtLogService {

    private final PtLogRepository ptLogRepository;
    private final PtLogExerciseRepository ptLogExerciseRepository;
    private final PtScheduleRepository ptScheduleRepository;
    private final ExerciseRepository exerciseRepository;

    @Transactional
    public Long createPtLog(PtLogCreateRequestDTO request, TrainerDTO trainer) {
        // PT 스케줄 조회
        PtSchedule ptSchedule = ptScheduleRepository.findByIdWithContractAndMembers(request.getPtScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다."));

        // PT 로그 중복 체크
        if (ptLogRepository.existsByPtScheduleId(request.getPtScheduleId())) {
            throw new IllegalArgumentException("이미 해당 PT 스케줄에 대한 로그가 존재합니다.");
        }

        // PT 로그 생성
        PtLog ptLog = PtLog.builder()
                .ptSchedule(ptSchedule)
                .member(ptSchedule.getPtContract().getMember())
                .trainer(ptSchedule.getPtContract().getTrainer())
                .feedback(request.getFeedback())
                .injuryCheck(request.isInjuryCheck())
                .nextPlan(request.getNextPlan())
                .build();

        // PT 로그 저장
        PtLog savedPtLog = ptLogRepository.save(ptLog);

        // 운동 로그 생성
        List<PtLogExercise> exercises = IntStream.range(0, request.getExercises().size())
                .<PtLogExercise>mapToObj(i -> {
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
                            .feedback(exerciseDto.getFeedback())
                            .build();
                })
                .toList();

        // 운동 로그 저장
        ptLogExerciseRepository.saveAll(exercises);

        return savedPtLog.getId();
    }

    @Transactional(readOnly = true)
    public PtLogResponseDTO getPtLog(Long id) {
        PtLog ptLog = ptLogRepository.findByIdWithMemberAndExercisesAndNotDeleted(id)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        PtLogResponseDTO responseDTO = PtLogResponseDTO.from(ptLog);
        responseDTO.setMemberId(ptLog.getMember().getId());
        responseDTO.setTrainerId(ptLog.getTrainer().getId());
        return responseDTO;
    }

    @Transactional(readOnly = true)
    public List<PtLogResponseDTO> getPtLogsByPtContractId(Long ptContractId) {
        List<PtLog> ptLogs = ptLogRepository.findByPtContractIdAndNotDeleted(ptContractId);
        return ptLogs.stream()
                .map(ptLog -> {
                    PtLogResponseDTO responseDTO = PtLogResponseDTO.from(ptLog);
                    responseDTO.setMemberId(ptLog.getMember().getId());
                    responseDTO.setTrainerId(ptLog.getTrainer().getId());
                    return responseDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updatePtLog(Long id, PtLogUpdateRequestDTO request, TrainerDTO trainer) {
        // PT 로그 조회
        PtLog ptLog = ptLogRepository.findByIdWithMemberAndExercises(id)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        // 권한 체크
        if (!ptLog.getTrainer().getId().equals(trainer.getId())) {
            throw new IllegalArgumentException("해당 PT 로그를 수정할 권한이 없습니다.");
        }

        // PT 로그 메타 정보만 수정
        ptLog.setFeedback(request.getFeedback());
        ptLog.setInjuryCheck(request.getInjuryCheck());
        ptLog.setNextPlan(request.getNextPlan());

        // 수정된 PT 로그 저장
        PtLog updatedPtLog = ptLogRepository.save(ptLog);

        // 수정된 PT 로그의 ID 반환
        return updatedPtLog.getId();
    }

    @Transactional
    public void deletePtLog(Long id, TrainerDTO trainer) {
        // PT 로그 조회
        PtLog ptLog = ptLogRepository.findByIdWithMemberAndExercises(id)
                .orElseThrow(() -> new IllegalArgumentException("PT 로그를 찾을 수 없습니다."));

        // 권한 체크
        if (!ptLog.getTrainer().getId().equals(trainer.getId())) {
            throw new IllegalArgumentException("해당 PT 로그를 삭제할 권한이 없습니다.");
        }

        // PT 로그 운동 목록 조회
        List<PtLogExercise> exercises = ptLog.getExercises();

        // PT 로그 운동들도 함께 삭제 처리
        for (PtLogExercise exercise : exercises) {
            exercise.setIsDeleted(true);
        }

        // PT 로그 운동 저장
        ptLogExerciseRepository.saveAll(exercises);

        // PT 로그 소프트 삭제
        ptLog.setIsDeleted(true);
        ptLogRepository.save(ptLog);
    }
} 