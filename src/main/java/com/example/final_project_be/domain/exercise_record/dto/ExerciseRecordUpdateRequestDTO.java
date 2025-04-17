package com.example.final_project_be.domain.exercise_record.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ExerciseRecordUpdateRequestDTO {
    private Long memberId;
    private Long exerciseId;
    private LocalDateTime date;
    private JsonNode recordData;
    private JsonNode memoData;
} 