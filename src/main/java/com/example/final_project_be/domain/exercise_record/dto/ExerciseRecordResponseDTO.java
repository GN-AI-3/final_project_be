package com.example.final_project_be.domain.exercise_record.dto;

import java.time.LocalDate;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRecordResponseDTO {
    private Long id;
    private Long memberId;
    private Long exerciseId;
    private String exerciseName;
    private LocalDate date;
    private JsonNode recordData;
    private JsonNode memoData;

    public static ExerciseRecordResponseDTO from(ExerciseRecord exerciseRecord) {
        ExerciseRecordResponseDTO dto = new ExerciseRecordResponseDTO();
        dto.setId(exerciseRecord.getId());
        dto.setMemberId(exerciseRecord.getMember().getId());
        dto.setExerciseId(exerciseRecord.getExercise().getId());
        dto.setExerciseName(exerciseRecord.getExercise().getName());
        dto.setDate(exerciseRecord.getDate());
        dto.setRecordData(exerciseRecord.getRecordData());
        dto.setMemoData(exerciseRecord.getMemoData());
        return dto;
    }
} 