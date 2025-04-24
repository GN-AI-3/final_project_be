package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PtContractRepository extends JpaRepository<PtContract, Long> {
    @Query("SELECT pc FROM PtContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE pc.id = :id")
    Optional<PtContract> findByIdWithMemberAndTrainer(@Param("id") Long id);

    @Query("SELECT pc FROM PtContract pc " +
            "JOIN FETCH pc.member m " +
            "WHERE pc.trainer.id = :trainerId " +
            "AND pc.status = :status " +
            "ORDER BY m.name ASC")
    List<PtContract> findByTrainerIdAndStatus(
            @Param("trainerId") Long trainerId,
            @Param("status") ContractStatus status
    );

    @Query("SELECT pc FROM PtContract pc " +
            "JOIN FETCH pc.member m " +
            "WHERE pc.trainer.id = :trainerId " +
            "ORDER BY m.name ASC")
    List<PtContract> findByTrainerId(@Param("trainerId") Long trainerId);
    
    // 회원의 가장 최근 활성 계약 조회
    Optional<PtContract> findTop1ByMemberAndStatusOrderByCreatedAtDesc(Member member, ContractStatus status);
    
    /**
     * 회원 ID와 트레이너 ID로 계약 존재 여부 확인
     * 
     * @param memberId 회원 ID
     * @param trainerId 트레이너 ID
     * @return 계약 존재 여부
     */
    boolean existsByMemberIdAndTrainerId(
            @Param("memberId") Long memberId, 
            @Param("trainerId") Long trainerId);
} 