package com.example.final_project_be.domain.exercise_record.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.exercise_record.repository.ExerciseRecordRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseRecordService {

    private final ExerciseRecordRepository exerciseRecordRepository;

    public ExerciseRecord saveExerciseRecord(
            Member member,
            Exercise exercise,
            LocalDateTime date,
            JsonNode recordData,
            JsonNode memoData
    ) {
        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
                .member(member)
                .exercise(exercise)
                .date(date)
                .recordData(recordData)
                .memoData(memoData)
                .build();

        return exerciseRecordRepository.save(exerciseRecord);
    }

    @Transactional(readOnly = true)
    public Optional<ExerciseRecord> findById(Long id) {
        return exerciseRecordRepository.findById(id);
    }
} 