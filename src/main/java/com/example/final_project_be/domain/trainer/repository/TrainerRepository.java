package com.example.final_project_be.domain.trainer.repository;

import com.example.final_project_be.domain.trainer.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByEmail(String email);
    boolean existsByEmail(String email);
} 