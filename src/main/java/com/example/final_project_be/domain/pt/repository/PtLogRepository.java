package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PtLogRepository extends JpaRepository<PtLog, Long> {
    <S extends PtLogExercise> List<S> saveAll(Iterable<S> entities);
} 