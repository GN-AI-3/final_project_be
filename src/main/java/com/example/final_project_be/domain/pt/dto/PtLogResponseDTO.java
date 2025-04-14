package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PtLogResponseDTO {
    private Long id;
    private Long ptScheduleId;
    private Long memberId;
    private String memberName;
    private Long trainerId;
    private String trainerName;
    private String feedback;
    private boolean injuryCheck;
    private String nextPlan;
    private List<ExerciseLogResponseDTO> exercises;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String createdBy;
    private String modifiedBy;

    public static PtLogResponseDTO from(PtLog ptLog) {
        return PtLogResponseDTO.builder()
                .id(ptLog.getId())
                .ptScheduleId(ptLog.getPtSchedule().getId())
                .memberId(ptLog.getMember().getId())
                .memberName(ptLog.getMember().getName())
                .trainerId(ptLog.getTrainer().getId())
                .trainerName(ptLog.getTrainer().getName())
                .feedback(ptLog.getFeedback())
                .injuryCheck(ptLog.isInjuryCheck())
                .nextPlan(ptLog.getNextPlan())
                .exercises(ptLog.getExercises().stream()
                        .map(ExerciseLogResponseDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(ptLog.getCreatedAt())
                .modifiedAt(ptLog.getModifiedAt())
                .createdBy(ptLog.getCreatedBy())
                .modifiedBy(ptLog.getModifiedBy())
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExerciseLogResponseDTO {
        private Long id;
        private Long exerciseId;
        private String exerciseName;
        private Integer sequence;
        private Integer sets;
        private Integer reps;
        private Integer weight;
        private Integer restTime;
        private String feedback;

        public static ExerciseLogResponseDTO from(PtLogExercise exercise) {
            return ExerciseLogResponseDTO.builder()
                    .id(exercise.getId())
                    .exerciseId(exercise.getExercise().getId())
                    .exerciseName(exercise.getExercise().getName())
                    .sequence(exercise.getSequence())
                    .sets(exercise.getSets())
                    .reps(exercise.getReps())
                    .weight(exercise.getWeight())
                    .restTime(exercise.getRestTime())
                    .feedback(exercise.getFeedback())
                    .build();
        }
    }
} 