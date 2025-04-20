package com.example.final_project_be.domain.pt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;

public interface PtLogExerciseRepository extends JpaRepository<PtLogExercise, Long> {
    List<PtLogExercise> findByPtLogsOrderBySequenceAsc(PtLog ptLog);
}