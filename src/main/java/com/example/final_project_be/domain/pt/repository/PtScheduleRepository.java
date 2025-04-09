package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PtScheduleRepository extends JpaRepository<PtSchedule, Long> {
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_Id(LocalDateTime startTime, LocalDateTime endTime, Long trainerId);

    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(LocalDateTime startTime, LocalDateTime endTime, Long trainerId, PtScheduleStatus status);

    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_Id(LocalDateTime startTime, LocalDateTime endTime, Long memberId);

    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_IdAndStatus(LocalDateTime startTime, LocalDateTime endTime, Long memberId, PtScheduleStatus status);
} 