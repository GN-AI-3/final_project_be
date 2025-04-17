package com.example.final_project_be.domain.schedule.repository;

import com.example.final_project_be.domain.schedule.entity.ScheduleAlarm;
import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ScheduleAlarm 엔티티에 대한 Repository 인터페이스
 */
@Repository
public interface ScheduleAlarmRepository extends JpaRepository<ScheduleAlarm, Long> {

    /**
     * 특정 대상, 알림 유형, 날짜에 대한 알림이 존재하는지 확인
     * 
     * @param targetType 알림 대상 유형 (MEMBER, TRAINER)
     * @param targetId 대상 ID
     * @param alarmType 알림 유형
     * @param targetDate 알림 대상 날짜
     * @return 알림이 존재하면 true
     */
    boolean existsByTargetTypeAndTargetIdAndAlarmTypeAndTargetDate(
            AlarmTargetType targetType, 
            Long targetId, 
            AlarmType alarmType, 
            LocalDate targetDate);
    
    /**
     * 특정 대상, 알림 유형, 날짜에 해당하는 알림 목록 조회
     */
    List<ScheduleAlarm> findByTargetTypeAndTargetIdAndAlarmTypeAndTargetDate(
            AlarmTargetType targetType,
            Long targetId,
            AlarmType alarmType,
            LocalDate targetDate);
}
