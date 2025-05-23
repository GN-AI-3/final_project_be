package com.example.final_project_be.domain.pt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.final_project_be.domain.pt.entity.PtLog;
import com.example.final_project_be.domain.pt.entity.PtLogExercise;

public interface PtLogExerciseRepository extends JpaRepository<PtLogExercise, Long> {
    List<PtLogExercise> findByPtLogsOrderBySequenceAsc(PtLog ptLog);

    @Query("SELECT ple FROM PtLogExercise ple WHERE ple.ptLogs.ptSchedule.id = :ptScheduleId")
    List<PtLogExercise> findByPtLogs_PtSchedule_Id(@Param("ptScheduleId") Long ptScheduleId);
}