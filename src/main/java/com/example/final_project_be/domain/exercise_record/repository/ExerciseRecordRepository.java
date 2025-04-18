package com.example.final_project_be.domain.exercise_record.repository;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    Optional<ExerciseRecord> findByMemberIdAndExerciseIdAndDate(Long memberId, Long exerciseId, LocalDate date);

    /**
     * 회원 ID와 날짜 범위로 운동 기록을 조회합니다.
     *
     * @param memberId 회원 ID
     * @param startTime 시작 날짜
     * @param endTime 종료 날짜
     * @return 운동 기록 목록
     */
    List<ExerciseRecord> findByMemberIdAndDateBetween(Long memberId, LocalDate startTime, LocalDate endTime);
}
