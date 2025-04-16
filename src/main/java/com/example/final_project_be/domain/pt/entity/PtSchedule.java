package com.example.final_project_be.domain.pt.entity;

import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
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
    @Setter
    private PtScheduleStatus status = PtScheduleStatus.SCHEDULED;

    @Column(name = "reason")
    @Setter
    private String reason;

    // 고객에게 보여줄 스케줄 예약 ID (YYMMDD_random_digits)
    @Column(name = "reservation_id")
    private String reservationId;

    @Column(name = "current_pt_count", nullable = false)
    @Setter
    private Integer currentPtCount;

    @Column(name = "is_deducted", nullable = false)
    @Builder.Default
    @Setter
    private Boolean isDeducted = true;

    @Transient
    @Getter
    @Setter
    private Long ptLogId;
}
