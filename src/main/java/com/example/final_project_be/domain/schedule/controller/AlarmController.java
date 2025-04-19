package com.example.final_project_be.domain.schedule.controller;

import com.example.final_project_be.domain.schedule.service.PtAlarmScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test/alarms")
public class AlarmController {

    private final PtAlarmScheduler ptAlarmScheduler;

    /**
     * 트레이너에게 내일 PT 일정 명단을 알려주는 알람을 테스트하는 API
     * @return 알람 발송 결과
     */
    @PostMapping("/trainer-summary")
    public ResponseEntity<String> testTrainerPtSummaryAlarms() {
        log.info("Manual test for trainer PT summary alarms initiated");
        ptAlarmScheduler.sendTrainerPtSummaryAlarms();
        return ResponseEntity.ok("트레이너 PT 일정 명단 알람 발송이 요청되었습니다.");
    }
} 