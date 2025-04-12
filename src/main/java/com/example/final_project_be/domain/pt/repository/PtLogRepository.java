package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PtLogRepository extends JpaRepository<PtLog, Long> {
    @Query("SELECT pl FROM PtLog pl " +
            "JOIN FETCH pl.member " +
            "JOIN FETCH pl.exercises e " +
            "JOIN FETCH e.exercise " +
            "WHERE pl.id = :id")
    Optional<PtLog> findByIdWithMemberAndExercises(@Param("id") Long id);
} 