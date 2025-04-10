package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PtContractRepository extends JpaRepository<PtContract, Long> {
    @Query("SELECT pc FROM PtContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE pc.id = :id")
    Optional<PtContract> findByIdWithMemberAndTrainer(@Param("id") Long id);
} 