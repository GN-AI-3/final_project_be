package com.example.final_project_be.domain.pt.entity;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    @Column(name = "created_by", nullable = false)
    private Long created_by;

    @Column(name = "modified_by")
    private Long modified_by;

    @OneToMany(mappedBy = "ptLogs", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PtLogExercise> exercises = new ArrayList<>();
}
