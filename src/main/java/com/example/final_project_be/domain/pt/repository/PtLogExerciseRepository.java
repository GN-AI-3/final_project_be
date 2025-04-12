package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtLogExerciseRepository extends JpaRepository<PtLogExercise, Long> {
} 