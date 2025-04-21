package com.example.final_project_be.domain.exercise_record.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ExerciseRecordGroupedResponseDTO {
    private String date;
    private List<ExerciseRecordDetailDTO> records;

    @Getter
    @Setter
    public static class ExerciseRecordDetailDTO {
        private Long exerciseId;
        private String exerciseName;
        private JsonNode recordData;
        private JsonNode memoData;
    }
} 