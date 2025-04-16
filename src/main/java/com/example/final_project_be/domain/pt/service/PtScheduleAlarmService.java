package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.util.FcmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtScheduleAlarmService {

    private final PtScheduleRepository ptScheduleRepository;
    private final FcmUtil fcmUtil;

    @Scheduled(cron = "0 0 9 * * *") // 매일 오전 9시 실행
    @Transactional
    public void sendDailyAlarms() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowStart = now.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime tomorrowEnd = tomorrowStart.withHour(23).withMinute(59).withSecond(59);

        List<PtSchedule> schedules = ptScheduleRepository.findSchedulesForDayBeforeAlarm(
                tomorrowStart, tomorrowEnd, now.withHour(0).withMinute(0).withSecond(0).withNano(0)
        );

        List<String> tokens = schedules.stream()
                .map(s -> s.getPtContract().getMember().getFcmToken())
                .filter(token -> token != null && !token.isBlank())
                .distinct()
                .toList();

        if (!tokens.isEmpty()) {
            String title = "📅 내일 PT 일정 알림";
            String body = "내일 예정된 PT 일정이 있어요!";

            fcmUtil.sendMulticast(tokens, title, body);
        }

        schedules.forEach(s -> s.setLastAlarmSentAt(now));
        }
    }