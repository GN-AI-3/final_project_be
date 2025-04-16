package com.example.final_project_be.domain.schedule.entity;

import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "schedule_alarm")
public class ScheduleAlarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MEMBER or TRAINER
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 20)
    private AlarmTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    // 예: PT_BEFORE, NO_DIET_LOG, NO_EXERCISE_LOG
    @Enumerated(EnumType.STRING)
    @Column(name = "alarm_type", nullable = false, length = 50)
    private AlarmType alarmType;

    // 관련된 일정 등 (예: pt_schedule_id)
    @Column(name = "related_entity_id")
    private Long relatedEntityId;

    // 기준 날짜 (예: 일정 날짜, 기록 확인 날짜 등)
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    // 실제 전송된 시각
    @CreationTimestamp
    @Column(name = "sent_at", nullable = false, updatable = false)
    private LocalDateTime sentAt;

    // 전송 상태 (성공, 실패 등 확장 가능)
    @Column(name = "status", length = 20)
    private String status;
}
