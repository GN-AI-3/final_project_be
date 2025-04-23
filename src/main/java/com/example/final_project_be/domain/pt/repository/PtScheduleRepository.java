package com.example.final_project_be.domain.pt.repository;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.querydsl.PtScheduleRepositoryCustom;
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
public interface PtScheduleRepository extends JpaRepository<PtSchedule, Long>, PtScheduleRepositoryCustom {
    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.member.id = :memberId")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_Id(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("memberId") Long memberId);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.member.id = :memberId AND ps.status = :status")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Member_IdAndStatus(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("memberId") Long memberId, @Param("status") PtScheduleStatus status);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.trainer.id = :trainerId")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_Id(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("trainerId") Long trainerId);

    @Query("SELECT ps FROM PtSchedule ps JOIN FETCH ps.ptContract pc JOIN FETCH pc.member JOIN FETCH pc.trainer WHERE ps.startTime BETWEEN :startTime AND :endTime AND pc.trainer.id = :trainerId AND ps.status = :status")
    List<PtSchedule> findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("trainerId") Long trainerId, @Param("status") PtScheduleStatus status);

    @Query("SELECT ps FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "WHERE ps.ptContract.id = :ptContractId " +
            "AND ps.startTime >= :startTime " +
            "ORDER BY ps.startTime ASC")
    List<PtSchedule> findByPtContractIdAndStartTimeAfter(@Param("ptContractId") Long ptContractId, @Param("startTime") LocalDateTime startTime);

    @Query("SELECT ps FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member m " +
            "JOIN FETCH pc.trainer t " +
            "WHERE ps.id = :id")
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
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member " +
            "JOIN FETCH pc.trainer " +
            "WHERE pc.id = :contractId " +
            "AND ps.status = :status " +
            "AND ((ps.startTime <= :endTime AND ps.endTime >= :startTime) " +
            "OR (ps.startTime >= :startTime AND ps.startTime < :endTime))")
    List<PtSchedule> findOverlappingSchedules(
            @Param("contractId") Long contractId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("status") PtScheduleStatus status
    );

    @Query("SELECT ps, (SELECT pl.id FROM PtLog pl WHERE pl.ptSchedule.id = ps.id) AS ptLogId " +
            "FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member m " +
            "JOIN FETCH pc.trainer t " +
            "WHERE ps.startTime BETWEEN :startTime AND :endTime " +
            "AND pc.member.id = :memberId " +
            "AND (:status is null OR ps.status = :status)")
    List<Object[]> findByStartTimeBetweenAndPtContract_Member_IdWithPtLog(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("memberId") Long memberId,
            @Param("status") PtScheduleStatus status);

    @Query("SELECT ps, (SELECT pl.id FROM PtLog pl WHERE pl.ptSchedule.id = ps.id) AS ptLogId " +
            "FROM PtSchedule ps " +
            "JOIN FETCH ps.ptContract pc " +
            "JOIN FETCH pc.member m " +
            "JOIN FETCH pc.trainer t " +
            "WHERE ps.startTime BETWEEN :startTime AND :endTime " +
            "AND pc.trainer.id = :trainerId " +
            "AND (:status is null OR ps.status = :status)")
    List<Object[]> findByStartTimeBetweenAndPtContract_Trainer_IdWithPtLog(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("trainerId") Long trainerId,
            @Param("status") PtScheduleStatus status);

    @Query("SELECT ps.currentPtCount FROM PtSchedule ps " +
            "WHERE ps.ptContract.id = :ptContractId " +
            "AND ps.startTime < :beforeTime " +
            "AND ps.isDeducted = true " +
            "ORDER BY ps.startTime DESC " +
            "LIMIT 1")
    Integer findPreviousPtCount(@Param("ptContractId") Long ptContractId, @Param("beforeTime") LocalDateTime beforeTime);

    Optional<PtSchedule> findById(Long id);

    /**
     * PT 계약 ID와 트레이너 ID로 스케줄이 존재하는지 확인합니다.
     * 트레이너가 해당 계약의 담당자인지 검증하는 데 사용됩니다.
     *
     * @param ptContractId PT 계약 ID
     * @param trainerId    트레이너 ID
     * @return 해당 계약과 트레이너로 된 스케줄이 존재하면 true, 아니면 false
     */
    @Query("SELECT CASE WHEN COUNT(ps) > 0 THEN true ELSE false END FROM PtSchedule ps " +
            "JOIN ps.ptContract pc " +
            "WHERE pc.id = :ptContractId AND pc.trainer.id = :trainerId")
    boolean existsByPtContractIdAndTrainerId(
            @Param("ptContractId") Long ptContractId, 
            @Param("trainerId") Long trainerId);
} 