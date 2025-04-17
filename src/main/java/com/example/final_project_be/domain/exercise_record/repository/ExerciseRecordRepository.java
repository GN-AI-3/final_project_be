package com.example.final_project_be.domain.exercise_record.repository;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {
    Optional<ExerciseRecord> findByMemberIdAndExerciseIdAndDate(Long memberId, Long exerciseId, LocalDateTime date);
}
