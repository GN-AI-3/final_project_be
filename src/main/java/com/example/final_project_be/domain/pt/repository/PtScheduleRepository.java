package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PtScheduleRepository extends JpaRepository<PtSchedule, Long> {
    List<PtSchedule> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    List<PtSchedule> findByStartTimeBetweenAndStatus(LocalDateTime startTime, LocalDateTime endTime, PtScheduleStatus status);
} 