package com.example.final_project_be.domain.trainer.repository;

import com.example.final_project_be.domain.trainer.entity.TrainerWorkingTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerWorkingTimeRepository extends JpaRepository<TrainerWorkingTime, Long> {
    Optional<TrainerWorkingTime> findByTrainerIdAndDayOfWeek(Long trainerId, Integer dayOfWeek);

    List<TrainerWorkingTime> findByTrainerId(Long trainerId);
} 