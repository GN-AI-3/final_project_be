package com.example.final_project_be.domain.trainer.entity;

import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "trainer_working_time",
        uniqueConstraints = @UniqueConstraint(columnNames = {"trainer_id", "day"}))
@Getter
@Setter
@NoArgsConstructor
public class TrainerWorkingTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @Enumerated(EnumType.STRING)
    @Column(name = "day", nullable = false)
    private DayOfWeek day;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "is_active")
    private Boolean isActive = true;
}