package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PtLogRepository extends JpaRepository<PtLog, Long> {
} 