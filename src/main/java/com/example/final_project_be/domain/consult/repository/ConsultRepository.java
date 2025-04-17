package com.example.final_project_be.domain.consult.repository;

import com.example.final_project_be.domain.consult.entity.Consult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsultRepository extends JpaRepository<Consult, Long> {
    
    /**
     * PT 계약 ID로 상담 정보를 조회합니다.
     * 
     * @param ptContractId PT 계약 ID
     * @return 해당 PT 계약에 대한 상담 정보
     */
    Optional<Consult> findByPtContractId(Long ptContractId);
    
    /**
     * PT 계약 ID로 상담 정보가 존재하는지 확인합니다.
     * 
     * @param ptContractId PT 계약 ID
     * @return 상담 정보 존재 여부
     */
    boolean existsByPtContractId(Long ptContractId);
    
    /**
     * 트레이너 ID로 해당 트레이너가 진행한 모든 상담 정보를 조회합니다.
     * 
     * @param trainerId 트레이너 ID
     * @return 트레이너가 진행한 상담 목록
     */
    @Query("SELECT c FROM Consult c WHERE c.ptContract.trainer.id = :trainerId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    java.util.List<Consult> findByTrainerId(Long trainerId);
    
    /**
     * 회원 ID로 해당 회원의 모든 상담 정보를 조회합니다.
     * 
     * @param memberId 회원 ID
     * @return 회원의 상담 목록
     */
    @Query("SELECT c FROM Consult c WHERE c.ptContract.member.id = :memberId AND c.isDeleted = false ORDER BY c.createdAt DESC")
    java.util.List<Consult> findByMemberId(Long memberId);
} 