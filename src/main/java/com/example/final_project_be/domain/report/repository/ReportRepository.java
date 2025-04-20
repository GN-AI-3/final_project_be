package com.example.final_project_be.domain.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.final_project_be.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
} 