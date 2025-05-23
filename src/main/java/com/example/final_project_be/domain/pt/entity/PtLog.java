package com.example.final_project_be.domain.pt.entity;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@DynamicUpdate
@SuperBuilder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pt_log")
public class PtLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_schedule_id")
    private PtSchedule ptSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    @Column(name = "feedback")
    private String feedback;

    @Column(name = "injury_check")
    private boolean injuryCheck;

    @Column(name = "next_plan")
    private String nextPlan;

    @OneToMany(mappedBy = "ptLogs", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PtLogExercise> exercises = new ArrayList<>();
}
