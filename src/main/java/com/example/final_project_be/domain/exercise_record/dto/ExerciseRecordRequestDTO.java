package com.example.final_project_be.domain.exercise_record.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRecordRequestDTO {
    private Long memberId;
    private Long exerciseId;
    private LocalDateTime date;
    private JsonNode recordData;
    private JsonNode memoData;
} 