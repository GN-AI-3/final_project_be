package com.example.final_project_be.domain.exercise_record.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordUpdateRequestDTO;
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
@Transactional(readOnly = true)
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

    @Transactional
    public ExerciseRecordResponseDTO updateExerciseRecord(ExerciseRecordUpdateRequestDTO requestDTO) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository
                .findByMemberIdAndExerciseIdAndDate(
                        requestDTO.getMemberId(),
                        requestDTO.getExerciseId(),
                        requestDTO.getDate()
                )
                .orElseThrow(() -> new RuntimeException("Exercise record not found"));

        // recordData가 있는 경우에만 업데이트
        if (requestDTO.getRecordData() != null) {
            exerciseRecord.setRecordData(requestDTO.getRecordData());
        }

        // memoData가 있는 경우에만 업데이트
        if (requestDTO.getMemoData() != null) {
            exerciseRecord.setMemoData(requestDTO.getMemoData());
        }

        ExerciseRecord updatedRecord = exerciseRecordRepository.save(exerciseRecord);
        return ExerciseRecordResponseDTO.from(updatedRecord);
    }
} 