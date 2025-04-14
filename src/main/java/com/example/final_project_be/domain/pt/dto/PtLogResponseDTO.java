package com.example.final_project_be.domain.pt.dto;

import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import lombok.*;

import java.time.ZoneId;
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
    private Long createdAt;
    private Long modifiedAt;
    private Long createdBy;
    private Long modifiedBy;

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
                        .filter(exercise -> !exercise.getIsDeleted())
                        .map(ExerciseLogResponseDTO::from)
                        .collect(Collectors.toList()))
                .createdAt(ptLog.getCreatedAt() != null ? ptLog.getCreatedAt().atZone(ZoneId.systemDefault()).toEpochSecond() : null)
                .modifiedAt(ptLog.getModifiedAt() != null ? ptLog.getModifiedAt().atZone(ZoneId.systemDefault()).toEpochSecond() : null)
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