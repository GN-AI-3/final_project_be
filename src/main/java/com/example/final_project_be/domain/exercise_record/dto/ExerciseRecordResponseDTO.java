package com.example.final_project_be.domain.exercise_record.dto;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExerciseRecordResponseDTO {
    private Long id;
    private Long memberId;
    private Long exerciseId;
    private String exerciseName;
    private LocalDateTime date;
    private JsonNode recordData;
    private JsonNode memoData;

    public static ExerciseRecordResponseDTO from(ExerciseRecord exerciseRecord) {
        return ExerciseRecordResponseDTO.builder()
                .id(exerciseRecord.getId())
                .memberId(exerciseRecord.getMember().getId())
                .exerciseId(exerciseRecord.getExercise().getId())
                .exerciseName(exerciseRecord.getExercise().getName())
                .date(exerciseRecord.getDate())
                .recordData(exerciseRecord.getRecordData())
                .memoData(exerciseRecord.getMemoData())
                .build();
    }
} 