package com.example.final_project_be.domain.trainer.repository;

import com.example.final_project_be.domain.trainer.entity.TrainerUnavailableTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainerUnavailableTimeRepository extends JpaRepository<TrainerUnavailableTime, Long> {
} 