package com.example.final_project_be.domain.schedule.service;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.schedule.entity.ScheduleAlarm;
import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import com.example.final_project_be.domain.schedule.repository.ScheduleAlarmRepository;
import com.example.final_project_be.domain.pt.repository.querydsl.PtScheduleRepositoryCustom;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.util.FcmUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PtAlarmScheduler {

    private final PtScheduleRepositoryCustom ptScheduleRepository;
    private final ScheduleAlarmRepository scheduleAlarmRepository;
    private final TrainerRepository trainerRepository;
    private final FcmUtil fcmUtil;

    @Scheduled(cron = "0 0 9 * * *", zone = "Asia/Seoul") // 매일 오전 9시 (한국 시간)
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
        }

        // === FCM 다중 전송 (회원만) ===
        if (!memberTokens.isEmpty()) {
            fcmUtil.sendMulticast(memberTokens, "📅 내일 PT 일정 알림", "내일 예정된 PT 일정이 있어요!");
        }

        // === 알림 로그 저장 ===
        scheduleAlarmRepository.saveAll(alarmLogs);

        log.info("Completed PT before day alarm scheduler: {} member notifications sent", 
                memberTokens.size());
    }
    
    /**
     * 트레이너에게 다음날 PT 일정 명단을 알려주는 알람
     * 매일 저녁 8시에 실행
     * PT 일정이 없는 트레이너에게도 "내일은 예정된 PT가 없습니다" 메시지를 전송
     */
    @Scheduled(cron = "0 0 20 * * *", zone = "Asia/Seoul") // 매일 저녁 8시 (한국 시간)
    @Transactional
    public void sendTrainerPtSummaryAlarms() {
        log.info("Starting trainer PT summary alarm scheduler");

        LocalDateTime now = LocalDateTime.now();
        LocalDate targetDate = now.plusDays(1).toLocalDate();
        
        // 날짜 포맷터
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // 다음날 전체 시간 범위
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(23, 59, 59);
        
        // 트레이너별 스케줄 조회 (SCHEDULED 및 CHANGED 상태 포함)
        Map<Long, List<PtSchedule>> trainerSchedulesMap = ptScheduleRepository.findSchedulesForTrainerSummary(start, end);
        log.info("Found PT schedules for {} trainers", trainerSchedulesMap.size());
        
        // 모든 트레이너 목록 조회
        List<Trainer> allTrainers = trainerRepository.findAll();
        log.info("Total trainers in system: {}", allTrainers.size());
        
        List<ScheduleAlarm> alarmLogs = new ArrayList<>();
        
        // 모든 트레이너에게 알림 전송
        for (Trainer trainer : allTrainers) {
            Long trainerId = trainer.getId();
            List<PtSchedule> trainerSchedules = trainerSchedulesMap.getOrDefault(trainerId, new ArrayList<>());
            
            // PT_BEFORE 알람 유형을 사용하여 이미 알림을 보냈는지 확인
            boolean alreadySent = scheduleAlarmRepository.existsByTargetTypeAndTargetIdAndAlarmTypeAndTargetDate(
                    AlarmTargetType.TRAINER, trainerId, AlarmType.PT_BEFORE, targetDate);
            
            if (alreadySent) {
                log.debug("Summary alarm already sent to trainer ID: {}", trainerId);
                continue;
            }
            
            // 트레이너 FCM 토큰 확인
            String trainerToken = trainer.getFcmToken();
            
            if (trainerToken == null || trainerToken.isBlank()) {
                log.warn("Trainer ID: {} has no valid FCM token. Skipping notification.", trainerId);
                continue;
            }
            
            log.info("Preparing to send alarm to trainer: id={}, name={}, token={}", 
                    trainerId, trainer.getName(), trainerToken);
            
            // 회원 목록 메시지 구성
            StringBuilder messageBody = new StringBuilder();
            messageBody.append(targetDate.format(dateFormatter));
            
            // 일정이 있는 경우와 없는 경우 메시지 분기
            if (trainerSchedules.isEmpty()) {
                messageBody.append(" 예정된 PT가 없습니다.");
                
                // FCM 전송 (PT 없음) - sendToDevice 메서드 사용
                log.info("Sending 'No PT scheduled' message to trainer ID: {}", trainerId);
                fcmUtil.sendToDevice(
                        trainerToken,
                        "📋 내일 PT 일정 알림",
                        messageBody.toString()
                );
                
                // 알림 로그 저장 (relatedEntityId는 null로 설정)
                alarmLogs.add(ScheduleAlarm.builder()
                        .targetType(AlarmTargetType.TRAINER)
                        .targetId(trainerId)
                        .alarmType(AlarmType.PT_BEFORE)
                        .targetDate(targetDate)
                        .status("SENT")
                        .build());
                
                log.info("Sent 'No PT scheduled' alarm to trainer ID: {}", trainerId);
                
            } else {
                // PT 일정이 있는 경우 명단 작성
                messageBody.append(" PT 일정 명단입니다.\n\n");
                
                // 시간순으로 정렬 (모든 일정을 함께 처리)
                List<PtSchedule> sortedSchedules = new ArrayList<>(trainerSchedules);
                sortedSchedules.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                
                // 모든 일정을 시간순으로 표시
                for (PtSchedule schedule : sortedSchedules) {
                    String memberName = schedule.getPtContract().getMember().getName();
                    String startTime = schedule.getStartTime().format(timeFormatter);
                    String endTime = schedule.getEndTime().format(timeFormatter);
                    String statusIndicator = "";
                    
                    // 변경된 일정인 경우 작은 표시 추가
                    if (schedule.getStatus() == com.example.final_project_be.domain.pt.enums.PtScheduleStatus.CHANGED) {
                        statusIndicator = " 🔄";
                    }
                    
                    messageBody.append("• ")
                            .append(startTime)
                            .append("~")
                            .append(endTime)
                            .append(" : ")
                            .append(memberName)
                            .append(statusIndicator)
                            .append("\n");
                }
                
                // FCM 전송 (PT 명단) - sendToDevice 메서드 사용
                log.info("Sending PT summary message to trainer ID: {}, schedules count: {}", 
                        trainerId, trainerSchedules.size());
                fcmUtil.sendToDevice(
                        trainerToken,
                        "📋 내일 PT 회원 명단",
                        messageBody.toString()
                );
                
                // 첫 번째 스케줄의 ID를 관련 엔티티 ID로 사용
                Long relatedScheduleId = trainerSchedules.get(0).getId();
                
                // PT_BEFORE 알람 유형을 사용하여 알림 로그 저장
                alarmLogs.add(ScheduleAlarm.builder()
                        .targetType(AlarmTargetType.TRAINER)
                        .targetId(trainerId)
                        .alarmType(AlarmType.PT_BEFORE)
                        .targetDate(targetDate)
                        .relatedEntityId(relatedScheduleId)
                        .status("SENT")
                        .build());
                
                log.info("Sent PT summary alarm to trainer ID: {} with {} schedules", trainerId, trainerSchedules.size());
            }
        }
        
        // 알림 로그 저장
        if (!alarmLogs.isEmpty()) {
            log.info("Saving {} alarm logs to database", alarmLogs.size());
            scheduleAlarmRepository.saveAll(alarmLogs);
        }
        
        log.info("Completed trainer PT summary alarm scheduler. Sent to {} trainers", alarmLogs.size());
    }

    /**
     * 트레이너에게 다음날 PT 일정 명단을 즉시 알려주는 메서드
     * sendTrainerPtSummaryAlarms와 동일한 로직이지만, 스케줄러가 아닌 직접 호출을 위한 메서드
     * 
     * @param trainerId 특정 트레이너 ID (null인 경우 모든 트레이너에게 알림 전송)
     * @return 알림을 보낸 트레이너 수
     */
    @Transactional
    public int sendTrainerPtSummaryAlarmsNow(Long trainerId) {
        log.info("Starting immediate trainer PT summary alarm - targetTrainerId: {}", trainerId);

        LocalDateTime now = LocalDateTime.now();
        LocalDate targetDate = now.plusDays(1).toLocalDate();
        
        // 날짜 포맷터
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // 다음날 전체 시간 범위
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(23, 59, 59);
        
        // 트레이너별 스케줄 조회 (SCHEDULED 및 CHANGED 상태 포함)
        Map<Long, List<PtSchedule>> trainerSchedulesMap = ptScheduleRepository.findSchedulesForTrainerSummary(start, end);
        log.info("Found PT schedules for {} trainers", trainerSchedulesMap.size());
        
        List<Trainer> trainers;
        // 특정 트레이너가 지정된 경우
        if (trainerId != null) {
            log.info("Sending alarm to specific trainer ID: {}", trainerId);
            trainers = trainerRepository.findById(trainerId)
                    .map(List::of)
                    .orElse(Collections.emptyList());
        } else {
            // 모든 트레이너 목록 조회
            trainers = trainerRepository.findAll();
            log.info("Total trainers in system: {}", trainers.size());
        }
        
        List<ScheduleAlarm> alarmLogs = new ArrayList<>();
        int sentCount = 0;
        
        // 대상 트레이너에게 알림 전송
        for (Trainer trainer : trainers) {
            Long currentTrainerId = trainer.getId();
            List<PtSchedule> trainerSchedules = trainerSchedulesMap.getOrDefault(currentTrainerId, new ArrayList<>());
            
            // 트레이너 FCM 토큰 확인
            String trainerToken = trainer.getFcmToken();
            
            if (trainerToken == null || trainerToken.isBlank()) {
                log.warn("Trainer ID: {} has no valid FCM token. Skipping notification.", currentTrainerId);
                continue;
            }
            
            log.info("Preparing to send alarm to trainer: id={}, name={}, token={}", 
                    currentTrainerId, trainer.getName(), trainerToken);
            
            // 회원 목록 메시지 구성
            StringBuilder messageBody = new StringBuilder();
            messageBody.append(targetDate.format(dateFormatter));
            
            // 일정이 있는 경우와 없는 경우 메시지 분기
            if (trainerSchedules.isEmpty()) {
                messageBody.append(" 예정된 PT가 없습니다.");
                
                // FCM 전송 (PT 없음) - sendToDevice 메서드 사용
                log.info("Sending 'No PT scheduled' message to trainer ID: {}", currentTrainerId);
                boolean success = fcmUtil.sendToDevice(
                        trainerToken,
                        "📋 내일 PT 일정 알림",
                        messageBody.toString()
                );
                
                if (success) {
                    sentCount++;
                    
                    // 알림 로그 저장 (relatedEntityId는 null로 설정)
                    alarmLogs.add(ScheduleAlarm.builder()
                            .targetType(AlarmTargetType.TRAINER)
                            .targetId(currentTrainerId)
                            .alarmType(AlarmType.PT_BEFORE)
                            .targetDate(targetDate)
                            .sentAt(LocalDateTime.now())
                            .status("SENT")
                            .build());
                    
                    log.info("Sent 'No PT scheduled' alarm to trainer ID: {}", currentTrainerId);
                }
                
            } else {
                // PT 일정이 있는 경우 명단 작성
                messageBody.append(" PT 일정 명단입니다.\n\n");
                
                // 시간순으로 정렬 (모든 일정을 함께 처리)
                List<PtSchedule> sortedSchedules = new ArrayList<>(trainerSchedules);
                sortedSchedules.sort((a, b) -> a.getStartTime().compareTo(b.getStartTime()));
                
                // 모든 일정을 시간순으로 표시
                for (PtSchedule schedule : sortedSchedules) {
                    String memberName = schedule.getPtContract().getMember().getName();
                    String startTime = schedule.getStartTime().format(timeFormatter);
                    String endTime = schedule.getEndTime().format(timeFormatter);
                    String statusIndicator = "";
                    
                    // 변경된 일정인 경우 작은 표시 추가
                    if (schedule.getStatus() == com.example.final_project_be.domain.pt.enums.PtScheduleStatus.CHANGED) {
                        statusIndicator = " 🔄";
                    }
                    
                    messageBody.append("• ")
                            .append(startTime)
                            .append("~")
                            .append(endTime)
                            .append(" : ")
                            .append(memberName)
                            .append(statusIndicator)
                            .append("\n");
                }
                
                // FCM 전송 (PT 명단) - sendToDevice 메서드 사용
                log.info("Sending PT summary message to trainer ID: {}, schedules count: {}", 
                        currentTrainerId, trainerSchedules.size());
                boolean success = fcmUtil.sendToDevice(
                        trainerToken,
                        "📋 내일 PT 회원 명단",
                        messageBody.toString()
                );
                
                if (success) {
                    sentCount++;
                    
                    // 첫 번째 스케줄의 ID를 관련 엔티티 ID로 사용
                    Long relatedScheduleId = trainerSchedules.get(0).getId();
                    
                    // PT_BEFORE 알람 유형을 사용하여 알림 로그 저장
                    alarmLogs.add(ScheduleAlarm.builder()
                            .targetType(AlarmTargetType.TRAINER)
                            .targetId(currentTrainerId)
                            .alarmType(AlarmType.PT_BEFORE)
                            .targetDate(targetDate)
                            .relatedEntityId(relatedScheduleId)
                            .sentAt(LocalDateTime.now())
                            .status("SENT")
                            .build());
                    
                    log.info("Sent PT summary alarm to trainer ID: {} with {} schedules", currentTrainerId, trainerSchedules.size());
                }
            }
        }
        
        // 알림 로그 저장
        if (!alarmLogs.isEmpty()) {
            log.info("Saving {} alarm logs to database", alarmLogs.size());
            scheduleAlarmRepository.saveAll(alarmLogs);
        }
        
        log.info("Completed immediate trainer PT summary alarm. Sent to {} trainers", sentCount);
        return sentCount;
    }
}