package com.example.final_project_be.domain.exercise_record.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordGroupedResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordUpdateRequestDTO;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.member.entity.Member;
import com.fasterxml.jackson.databind.JsonNode;

public interface ExerciseRecordService {
    ExerciseRecord saveExerciseRecord(
            Member member,
            Exercise exercise,
            LocalDate date,
            JsonNode recordData,
            JsonNode memoData
    );

    Optional<ExerciseRecord> findById(Long id);

    ExerciseRecordResponseDTO updateExerciseRecord(ExerciseRecordUpdateRequestDTO requestDTO);

    /**
     * 지정된 기간 내의 회원의 운동 기록을 날짜별로 그룹화하여 조회합니다.
     *
     * @param memberId 회원 ID
     * @param startTime 시작 날짜 (yyyy-MM-dd 형식)
     * @param endTime 종료 날짜 (yyyy-MM-dd 형식)
     * @return 날짜별로 그룹화된 운동 기록 목록
     */
    List<ExerciseRecordGroupedResponseDTO> getExerciseRecordsGroupedByDate(
            Long memberId, LocalDate startTime, LocalDate endTime);
} 