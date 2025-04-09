package com.example.final_project_be.domain.pt.entity;

import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@DynamicUpdate
@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pt_schedule")
public class PtSchedule extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_contract_id")
    private PtContract ptContract;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private PtScheduleStatus status = PtScheduleStatus.SCHEDULED;

    @Column(name = "reason")
    private String reason;

    // 고객에게 보여줄 스케줄 예약 ID (YYMMDD_random_digits)
    @Column(name = "reservation_id")
    private String reservationId;

    public Integer getCurrentPtCount() {
        return ptContract.getUsedCount() + 1;
    }
}
