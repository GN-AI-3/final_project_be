package com.example.final_project_be.domain.report.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.final_project_be.domain.report.entity.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("SELECT r FROM Report r WHERE r.ptContract.id = :ptContractId AND r.createdAt IN " +
           "(SELECT MAX(r2.createdAt) FROM Report r2 WHERE r2.ptContract.id = :ptContractId) " +
           "OR r.createdAt IN " +
           "(SELECT MIN(r3.createdAt) FROM Report r3 WHERE r3.ptContract.id = :ptContractId) " +
           "ORDER BY r.createdAt DESC")
    List<Report> findLatestAndOldestByPtContractId(@Param("ptContractId") Long ptContractId);
} 