package com.example.final_project_be.domain.schedule.service;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.schedule.entity.ScheduleAlarm;
import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import com.example.final_project_be.domain.schedule.repository.ScheduleAlarmRepository;
import com.example.final_project_be.domain.pt.repository.querydsl.PtScheduleRepositoryCustom;
import com.example.final_project_be.util.FcmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtAlarmScheduler {

    private final PtScheduleRepositoryCustom ptScheduleRepository;
    private final ScheduleAlarmRepository scheduleAlarmRepository;
    private final FcmUtil fcmUtil;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시
    @Transactional
    public void sendPtBeforeAlarms() {
        log.info("Starting PT before day alarm scheduler");

        LocalDateTime now = LocalDateTime.now();
        LocalDate targetDate = now.plusDays(1).toLocalDate();
        LocalDate today = now.toLocalDate();

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(23, 59, 59);

        List<PtSchedule> schedules = ptScheduleRepository.findSchedulesForDayBeforeAlarm(start, end, today);
        log.info("Found {} schedules requiring alarms", schedules.size());

        // === 알림 대상 분리 ===
        List<String> memberTokens = new java.util.ArrayList<>();
        List<String> trainerTokens = new java.util.ArrayList<>();
        List<ScheduleAlarm> alarmLogs = new java.util.ArrayList<>();

        for (PtSchedule schedule : schedules) {
            Long scheduleId = schedule.getId();
            LocalDateTime ptTime = schedule.getStartTime();
            LocalDate targetDay = ptTime.toLocalDate();

            // === 회원 대상자 ===
            var member = schedule.getPtContract().getMember();
            Long memberId = member.getId();
            String memberToken = member.getFcmToken();

            if (memberToken != null && !memberToken.isBlank()) {
                boolean alreadySent = scheduleAlarmRepository.existsByTargetTypeAndTargetIdAndAlarmTypeAndTargetDate(
                        AlarmTargetType.MEMBER, memberId, AlarmType.PT_BEFORE, targetDay);
                if (!alreadySent) {
                    memberTokens.add(memberToken);
                    alarmLogs.add(ScheduleAlarm.builder()
                            .targetType(AlarmTargetType.MEMBER)
                            .targetId(memberId)
                            .alarmType(AlarmType.PT_BEFORE)
                            .targetDate(targetDay)
                            .relatedEntityId(scheduleId)
                            .status("SENT")
                            .build());
                }
            }

            // === FCM 다중 전송 ===
            if (!memberTokens.isEmpty()) {
                fcmUtil.sendMulticast(memberTokens, "📅 내일 PT 일정 알림", "내일 예정된 PT 일정이 있어요!");
            }

            // === 알림 로그 저장 ===
            scheduleAlarmRepository.saveAll(alarmLogs);

            log.info("Completed PT before day alarm scheduler: {} member", memberTokens.size());
        }
    }
}