package com.example.final_project_be.domain.exercise_record.repository;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRecordRepository extends JpaRepository<ExerciseRecord, Long> {

}
