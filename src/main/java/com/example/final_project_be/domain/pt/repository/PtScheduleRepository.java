package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PtScheduleRepository extends JpaRepository<PtSchedule, Long> {
    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.member.id = :memberId")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_Id(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("memberId") Long memberId);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.member.id = :memberId AND ps.status = :status")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_IdAndStatus(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("memberId") Long memberId, @Param("status") PtScheduleStatus status);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.trainer.id = :trainerId")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_Id(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("trainerId") Long trainerId);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.trainer.id = :trainerId AND ps.status = :status")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("trainerId") Long trainerId, @Param("status") PtScheduleStatus status);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.ptContract.id = :ptContractId AND ps.status = :status")
    List<PtSchedule> findByPtContractIdAndStatus(@Param("ptContractId") Long ptContractId, @Param("status") PtScheduleStatus status);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.id = :id")
    Optional<PtSchedule> findByIdWithContractAndMembers(@Param("id") Long id);

    @Query("SELECT ps FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member " +
            "JOIN FETCH pc.trainer " +
            "WHERE ps.startTime <= :endTime " +
            "AND pc.id IN " +
            "(SELECT DISTINCT pc2.id FROM PtSchedule ps2 " +
            "JOIN ps2.ptContract pc2 " +
            "WHERE ps2.startTime BETWEEN :startTime AND :endTime " +
            "AND (:status is null OR ps2.status = :status) " +
            "AND pc2.member.id = :memberId) " +
            "ORDER BY ps.startTime ASC")
    List<PtSchedule> findSchedulesForCountCalculationByMember(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") PtScheduleStatus status,
            @Param("memberId") Long memberId
    );

    @Query("SELECT ps FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member " +
            "JOIN FETCH pc.trainer " +
            "WHERE ps.startTime <= :endTime " +
            "AND pc.id IN " +
            "(SELECT DISTINCT pc2.id FROM PtSchedule ps2 " +
            "JOIN ps2.ptContract pc2 " +
            "WHERE ps2.startTime BETWEEN :startTime AND :endTime " +
            "AND (:status is null OR ps2.status = :status) " +
            "AND pc2.trainer.id = :trainerId) " +
            "ORDER BY ps.startTime ASC")
    List<PtSchedule> findSchedulesForCountCalculationByTrainer(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") PtScheduleStatus status,
            @Param("trainerId") Long trainerId
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM PtSchedule ps " +
            "WHERE ps.ptContract.id = :contractId " +
            "AND ps.status = :status " +
            "AND ((ps.startTime <= :endTime AND ps.endTime >= :startTime) " +
            "OR (ps.startTime >= :startTime AND ps.startTime < :endTime))")
    List<PtSchedule> findOverlappingSchedules(
            @Param("contractId") Long contractId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") PtScheduleStatus status
    );
} 