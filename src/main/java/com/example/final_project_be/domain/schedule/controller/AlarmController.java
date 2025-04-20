package com.example.final_project_be.domain.schedule.controller;

import com.example.final_project_be.domain.schedule.service.PtAlarmScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
    
    /**
     * 트레이너에게 내일 PT 일정 명단을 즉시 보내는 API
     * 특정 트레이너 ID를 지정하거나 모든 트레이너에게 발송 가능
     * 
     * @param trainerId 알림을 보낼 트레이너 ID (선택적)
     * @return 알람 발송 결과
     */
    @PostMapping("/send-trainer-summary-now")
    public ResponseEntity<Map<String, Object>> sendTrainerPtSummaryNow(
            @RequestParam(required = false) Long trainerId) {
        
        log.info("Immediate trainer PT summary alarm requested - trainerId: {}", 
                trainerId != null ? trainerId : "all");
        
        int sentCount = ptAlarmScheduler.sendTrainerPtSummaryAlarmsNow(trainerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "트레이너 PT 일정 명단 알람이 성공적으로 발송되었습니다.");
        response.put("sentCount", sentCount);
        response.put("targetTrainerId", trainerId);
        
        return ResponseEntity.ok(response);
    }
} 