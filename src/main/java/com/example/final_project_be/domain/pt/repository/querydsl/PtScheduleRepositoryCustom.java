package com.example.final_project_be.domain.pt.repository.querydsl;

import com.example.final_project_be.domain.pt.entity.PtSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface PtScheduleRepositoryCustom {
    
    /**
     * 특정 기간 내 예정된 PT 스케줄 중 하루 전 알림이 아직 전송되지 않은 스케줄 목록을 조회합니다.
     * 
     * @param start 조회 시작 시간
     * @param end 조회 종료 시간
     * @param today 오늘 날짜 (알림 전송 기준일)
     * @return 알림이 필요한 PT 스케줄 목록
     */
    List<PtSchedule> findSchedulesForDayBeforeAlarm(LocalDateTime start, LocalDateTime end, LocalDate today);


    /**
     * 스케줄이 CANCELLED 상태이고,
     * 당일(today) 또는 내일(today.plusDays(1))에 예정된 스케줄이면서
     * 아직 Trainer에게 PT_CANCEL 알림이 전송되지 않은 스케줄 목록을 조회한다.
     *
     * @param today 기준 날짜
     * @return 알림이 필요한 취소 스케줄 목록
     */
    List<PtSchedule> findCancelledSchedulesDayOfOrDayBefore(LocalDate today);


}
