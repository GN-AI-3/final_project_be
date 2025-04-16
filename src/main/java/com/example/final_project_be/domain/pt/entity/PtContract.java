package com.example.final_project_be.domain.pt.entity;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.pt.enums.ContractStatus;
import com.example.final_project_be.domain.trainer.entity.Trainer;
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
@Table(name = "pt_contract")
public class PtContract extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(name = "total_count", nullable = false)
    private Integer totalCount;

    @Column(name = "used_count", nullable = false)
    @Setter
    @Builder.Default
    private Integer usedCount = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    @Setter
    private ContractStatus status = ContractStatus.ACTIVE;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "memo")
    private String memo;

    public Integer getRemainingCount() {
        return totalCount - usedCount;
    }

    public boolean isActive() {
        return status == ContractStatus.ACTIVE;
    }

    public void decrementUsedCount() {
        if (this.usedCount > 0) {
            this.usedCount--;
        }
    }
}
