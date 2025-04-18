package com.example.final_project_be.domain.exercise_record.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExerciseRecordRequestDTO {
    private Long memberId;
    private Long exerciseId;
    private LocalDate date;
    private JsonNode recordData;
    private JsonNode memoData;
} 