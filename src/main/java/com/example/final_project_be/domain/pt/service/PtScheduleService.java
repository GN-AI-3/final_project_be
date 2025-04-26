package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import com.example.final_project_be.domain.member.service.MemberService;
import com.example.final_project_be.domain.pt.dto.PtScheduleChangeRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleCreateRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleResponseDTO;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.domain.schedule.entity.ScheduleAlarm;
import com.example.final_project_be.domain.schedule.enums.AlarmTargetType;
import com.example.final_project_be.domain.schedule.enums.AlarmType;
import com.example.final_project_be.domain.schedule.repository.ScheduleAlarmRepository;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.entity.TrainerUnavailableTime;
import com.example.final_project_be.domain.trainer.entity.TrainerWorkingTime;
import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.domain.trainer.service.TrainerScheduleService;
import com.example.final_project_be.util.FcmUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;
    private final PtContractRepository ptContractRepository;
    private final ScheduleAlarmRepository scheduleAlarmRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MemberService memberService;
    private final FcmUtil fcmUtil;
    private final TrainerScheduleService trainerScheduleService;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 만료된 스케줄을 완료 처리합니다.
     * 매 60분마다 실행됩니다.
     */
    @Transactional
    @Scheduled(fixedRate = 60 * 60 * 1000) // 60분마다 실행
    public void updateExpiredSchedules() {
        try {
            int updatedCount = entityManager
                    .createQuery("UPDATE PtSchedule p SET p.status = 'COMPLETED' WHERE p.startTime < CURRENT_TIMESTAMP AND p.status = 'SCHEDULED'")
                    .executeUpdate();
            log.info("지난 스케줄 {}건이 완료 처리되었습니다.", updatedCount);
        } catch (Exception e) {
            log.error("스케줄 업데이트 중 오류 발생", e);
        }
    }

    @Transactional(readOnly = true)
    public List<PtScheduleResponseDTO> getSchedulesByDateRange(
            @NonNull LocalDateTime startTime,
            @NonNull LocalDateTime endTime,
            PtScheduleStatus status,
            Long memberId,
            Long trainerId) {

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        List<PtSchedule> schedules;
        List<Object[]> results;

        if (memberId != null) {
            results = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_IdWithPtLog(
                    startTime, endTime, memberId, status
            );
        } else if (trainerId != null) {
            results = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdWithPtLog(
                    startTime, endTime, trainerId, status
            );
        } else {
            throw new IllegalArgumentException("회원 ID 또는 트레이너 ID 중 하나는 필수입니다.");
        }

        schedules = results.stream()
                .map(result -> {
                    PtSchedule schedule = (PtSchedule) result[0];
                    schedule.setPtLogId((Long) result[1]);
                    return schedule;
                })
                .collect(Collectors.toList());

        return schedules.stream()
                .map(PtScheduleResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSchedule(PtScheduleCreateRequestDTO request, Long memberId, boolean shouldCheckRemaining) {
        // 계약 유효성 검사
        PtContract contract = validateContract(request.getPtContractId());

        // 요청한 회원이 계약의 실제 회원인지 확인 (using memberId)
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        if (!contract.getMember().equals(member)) {
            throw new IllegalArgumentException("해당 PT 계약에 대한 권한이 없습니다.");
        }

        if (shouldCheckRemaining) {
            // 남은 횟수 체크
            if (contract.getRemainingCount() <= 0) {
                throw new IllegalArgumentException("남은 PT 횟수가 없습니다.");
            }
            // 횟수 차감
            contract.setUsedCount(contract.getUsedCount() + 1);
            ptContractRepository.save(contract);
        }

        // 시간 유효성 검사
        LocalDateTime startTime = convertToLocalDateTime(request.getStartTime());
        LocalDateTime endTime = request.getEndTime() != null ?
                convertToLocalDateTime(request.getEndTime()) :
                startTime.plusHours(1);

        // 트레이너 가용 시간 체크
        Trainer trainer = contract.getTrainer();
        List<TrainerWorkingTime> workingTimes = trainerScheduleService.getWorkingTimeEntities(trainer.getId());
        List<TrainerUnavailableTime> unavailableTimes = trainerScheduleService.getUnavailableTimeEntities(
                trainer.getId(), startTime, endTime);
        List<PtSchedule> ptSchedules = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(
                startTime, endTime, trainer.getId(), PtScheduleStatus.SCHEDULED);

        DayOfWeek dayOfWeek = DayOfWeek.values()[startTime.getDayOfWeek().getValue() - 1];
        TrainerWorkingTime workingTime = workingTimes.stream()
                .filter(wt -> wt.getDay() == dayOfWeek)
                .findFirst()
                .orElse(null);

        if (!trainerScheduleService.isAvailableTime(workingTime, unavailableTimes, ptSchedules, startTime, endTime)) {
            throw new IllegalArgumentException("선택한 시간에 트레이너가 예약 가능한 상태가 아닙니다.");
        }

        // 현재 PT 회차 계산
        int currentCount = calculatePreviousPtCount(request.getPtContractId(), startTime);

        // PT 스케줄 생성
        PtSchedule ptSchedule = PtSchedule.builder()
                .ptContract(contract)
                .startTime(startTime)
                .endTime(endTime)
                .status(PtScheduleStatus.SCHEDULED)
                .isDeducted(true)
                .currentPtCount(currentCount + 1)
                .build();

        PtSchedule savedSchedule = ptScheduleRepository.save(ptSchedule);

        // 회차 재계산
        recalculatePtCounts(contract.getId(), startTime);
        
        // 당일 또는 내일 PT일 경우 알림 전송
        LocalDate ptDate = startTime.toLocalDate();
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        if (ptDate.isEqual(today) || ptDate.isEqual(tomorrow)) {
            sendNewPtScheduleNotification(savedSchedule);
        }

        return savedSchedule.getId();
    }
    
    /**
     * PT 스케줄 추가 시 알림을 보냅니다.
     * 당일이나 다음날 PT 추가 시에만 호출됩니다.
     *
     * @param schedule 추가된 PT 스케줄
     */
    @Transactional
    public void sendNewPtScheduleNotification(PtSchedule schedule) {
        log.info("새로운 PT 스케줄 알림 전송 시작...");
        
        LocalDateTime ptTime = schedule.getStartTime();
        LocalDate ptDate = ptTime.toLocalDate();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // 트레이너 알림
        Trainer trainer = schedule.getPtContract().getTrainer();
        String trainerToken = trainer.getFcmToken();
        String memberName = schedule.getPtContract().getMember().getName();
        
        if (trainerToken != null && !trainerToken.isBlank()) {
            String title = "🆕 새로운 PT 일정 추가";
            String body = String.format(
                    "%s 회원님과 %s에 PT 일정이 추가되었습니다.",
                    memberName,
                    ptTime.format(dateTimeFormatter)
            );
            
            // FCM 전송
            fcmUtil.sendPush(trainerToken, title, body);
            
            // 알림 로그 저장
            ScheduleAlarm trainerAlarm = ScheduleAlarm.builder()
                    .targetType(AlarmTargetType.TRAINER)
                    .targetId(trainer.getId())
                    .alarmType(AlarmType.PT_NEW)
                    .targetDate(ptDate)
                    .relatedEntityId(schedule.getId())
                    .status("SENT")
                    .build();
            
            scheduleAlarmRepository.save(trainerAlarm);
            log.info("트레이너에게 새 PT 일정 알림 전송 완료: {}", trainer.getId());
        }
        
        // 회원 알림
        var member = schedule.getPtContract().getMember();
        String memberToken = member.getFcmToken();
        String trainerName = trainer.getName();
        
        if (memberToken != null && !memberToken.isBlank()) {
            String title = "🆕 새로운 PT 일정 추가";
            String body = String.format(
                    "%s 트레이너님과 %s에 PT 일정이 추가되었습니다.",
                    trainerName,
                    ptTime.format(dateTimeFormatter)
            );
            
            // FCM 전송
            fcmUtil.sendPush(memberToken, title, body);
            
            // 알림 로그 저장
            ScheduleAlarm memberAlarm = ScheduleAlarm.builder()
                    .targetType(AlarmTargetType.MEMBER)
                    .targetId(member.getId())
                    .alarmType(AlarmType.PT_NEW)
                    .targetDate(ptDate)
                    .relatedEntityId(schedule.getId())
                    .status("SENT")
                    .build();
            
            scheduleAlarmRepository.save(memberAlarm);
            log.info("회원에게 새 PT 일정 알림 전송 완료: {}", member.getId());
        }
    }

    @Transactional
    public Long cancelSchedule(Long scheduleId, String reason, Long memberId, Long trainerId) {
        PtSchedule schedule = ptScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));
        validateScheduleModification(schedule, LocalDateTime.now(), true, memberId, trainerId);

        // 스케줄 취소
        schedule.setStatus(PtScheduleStatus.CANCELLED);
        schedule.setReason(reason);
        schedule.setIsDeducted(false);

        // 회차 정보 업데이트
        recalculatePtCounts(schedule.getPtContract().getId(), schedule.getStartTime());

        // PT 계약 테이블의 사용 횟수 감소
        PtContract contract = schedule.getPtContract();
        contract.setUsedCount(contract.getUsedCount() - 1);
        ptContractRepository.save(contract);

        return schedule.getId();
    }

    @Transactional
    public void sendCancelAlarm(Long scheduleId, String reason) {
        log.info("cancel alarm start...");

        PtSchedule schedule = ptScheduleRepository.findByIdWithContractAndMembers(scheduleId)
                .orElseThrow(() -> {
                    log.error("❗ scheduleId {}에 해당하는 PT 스케줄을 찾을 수 없습니다.", scheduleId);
                    return new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다.");
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ptTime = schedule.getStartTime();
        LocalDate ptDate = ptTime.toLocalDate();
        LocalDate today = now.toLocalDate();

        boolean isSameDay = ptDate.isEqual(today);
        boolean isDayBefore = ptDate.minusDays(1).isEqual(today);
        log.info("isSameDay: {}, isDayBefore: {}", isSameDay, isDayBefore);

        if (isSameDay || isDayBefore) {
            var trainer = schedule.getPtContract().getTrainer();
            String trainerToken = trainer.getFcmToken();

            if (trainerToken != null && !trainerToken.isBlank()) {
                String formattedTime = ptTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                String memberName = schedule.getPtContract().getMember().getName();
                int remainingCount = schedule.getPtContract().getRemainingCount();

                String title = "❗ PT 취소 알림";
                String body = String.format(
                        "회원 %s님의 %s 예정 PT가 취소되었습니다. 남은 회차: %d회\n사유: %s",
                        memberName, formattedTime, remainingCount, reason
                );

                // 🔔 FCM 전송
                fcmUtil.sendPush(trainerToken, title, body);

                // 📝 알림 로그 저장
                ScheduleAlarm alarm = ScheduleAlarm.builder()
                        .targetType(AlarmTargetType.TRAINER)
                        .targetId(trainer.getId())
                        .alarmType(AlarmType.PT_CANCEL)
                        .targetDate(ptDate)
                        .relatedEntityId(schedule.getId())
                        .status("SENT")
                        .build();

                scheduleAlarmRepository.save(alarm);
                log.info("cancel alarm finished.");
            }
        }
    }


    @Transactional
    public Long changeSchedule(Long scheduleId, PtScheduleChangeRequestDTO request, Long memberId, Long trainerId) {
        // Fetch the existing schedule WITH its contract and associated entities
        PtSchedule existingSchedule = ptScheduleRepository.findByIdWithContractAndMembers(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));

        // Validate modification rights and rules
        validateScheduleModification(existingSchedule, LocalDateTime.now(), false, memberId, trainerId);

        // 기존 스케줄 취소 처리 (횟수 복구 없음)
        existingSchedule.setStatus(PtScheduleStatus.CHANGED);
        existingSchedule.setReason("스케줄 변경으로 인한 취소");
        existingSchedule.setIsDeducted(false); // 횟수 차감 해제됨 (변경 시 횟수 소모 안 함)
        ptScheduleRepository.save(existingSchedule);

        // 계약 유효성 검사 (동일 계약으로만 변경 가능)
        PtContract contract = existingSchedule.getPtContract();

        // 새로운 스케줄 생성 DTO 준비
        PtScheduleCreateRequestDTO createRequest = PtScheduleCreateRequestDTO.builder()
                .ptContractId(contract.getId())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        // 새로운 스케줄 생성 (횟수 차감 없음, 요청자는 회원으로 간주)
        // Note: Determining the actual user (member or trainer) making the change might need refinement.
        // Here, we assume the member linked to the contract is the intended user for createSchedule validation.
        Long newScheduleId = createSchedule(createRequest, contract.getMember().getId(), false);

        // 기존 스케줄의 회차 재계산
        recalculatePtCounts(contract.getId(), existingSchedule.getStartTime());
        // 새 스케줄 포함 회차 재계산
        PtSchedule newSchedule = ptScheduleRepository.findById(newScheduleId)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve newly created schedule"));
        recalculatePtCounts(contract.getId(), newSchedule.getStartTime());

        return newScheduleId;
    }

    @Transactional
    public void sendChangeAlarm(Long scheduleId) {
        log.info("change alarm start...");

        PtSchedule schedule = ptScheduleRepository.findByIdWithContractAndMembers(scheduleId)
                .orElseThrow(() -> {
                    log.error("❗ scheduleId {}에 해당하는 PT 스케줄을 찾을 수 없습니다.", scheduleId);
                    return new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다.");
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ptTime = schedule.getStartTime();
        LocalDate ptDate = ptTime.toLocalDate();
        LocalDate today = now.toLocalDate();

        boolean isSameDay = ptDate.isEqual(today);
        boolean isDayBefore = ptDate.minusDays(1).isEqual(today);
        log.info("isSameDay: {}, isDayBefore: {}", isSameDay, isDayBefore);

        if (isSameDay || isDayBefore) {
            var trainer = schedule.getPtContract().getTrainer();
            String trainerToken = trainer.getFcmToken();

            if (trainerToken != null && !trainerToken.isBlank()) {
                String formattedTime = ptTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                String memberName = schedule.getPtContract().getMember().getName();
                int remainingCount = schedule.getPtContract().getRemainingCount();

                String title = "🔁 PT 일정 변경 알림";
                String body = String.format(
                        "회원 %s님의 PT 일정이 변경되었습니다. 새 일정: %s\n남은 회차: %d회",
                        memberName, formattedTime, remainingCount
                );

                // FCM 전송
                fcmUtil.sendPush(trainerToken, title, body);

                // 알림 로그 저장
                ScheduleAlarm alarm = ScheduleAlarm.builder()
                        .targetType(AlarmTargetType.TRAINER)
                        .targetId(trainer.getId())
                        .alarmType(AlarmType.PT_CHANGE)
                        .targetDate(ptDate)
                        .relatedEntityId(schedule.getId())
                        .status("SENT")
                        .build();

                scheduleAlarmRepository.save(alarm);
                log.info("change alarm finished.");
            }
        }
    }


    /**
     * PT 계약의 특정 시점 이후의 모든 스케줄 회차를 재계산합니다.
     *
     * @param ptContractId PT 계약 ID
     * @param startTime 이 시점 이후의 스케줄들만 재계산
     */
    @Transactional
    public void recalculatePtCounts(Long ptContractId, LocalDateTime startTime) {
        List<PtSchedule> schedules = ptScheduleRepository.findByPtContractIdAndStartTimeAfter(
                        ptContractId,
                        startTime
                ).stream()
                .toList();

        // 시작 시점 직전의 회차 가져오기
        int currentCount = calculatePreviousPtCount(ptContractId, startTime);

        // 이후 스케줄들의 회차 업데이트
        for (PtSchedule schedule : schedules) {
            if (schedule.getIsDeducted()) {
                currentCount++;
                schedule.setCurrentPtCount(currentCount);
            } else {
                schedule.setCurrentPtCount(currentCount);
            }
        }
    }

    /**
     * 특정 시점 이전까지의 PT 회차를 계산합니다.
     */
    private int calculatePreviousPtCount(Long ptContractId, LocalDateTime beforeTime) {
        return ptScheduleRepository.findPreviousPtCount(ptContractId, beforeTime);
    }

    /**
     * Checks if the provided memberId or trainerId has authority over the given schedule.
     * This method ONLY checks the relationship between the user ID and the contract.
     */
    private void validateScheduleAuthority(PtSchedule schedule, Long memberId, Long trainerId) {
        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            if (!schedule.getPtContract().getMember().equals(member)) {
                throw new IllegalArgumentException("해당 스케줄에 대한 권한이 없습니다.");
            }
        } else if (trainerId != null) {
            Trainer trainer = trainerRepository.findById(trainerId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));
            if (!schedule.getPtContract().getTrainer().equals(trainer)) {
                throw new IllegalArgumentException("해당 스케줄에 대한 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("회원 또는 트레이너 ID가 필요합니다.");
        }
    }

    @Transactional(readOnly = true)
    public PtScheduleResponseDTO getPtScheduleById(Long ptScheduleId) {
        PtSchedule ptSchedule = ptScheduleRepository.findByIdWithContractAndMembers(ptScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다."));
        return PtScheduleResponseDTO.from(ptSchedule);
    }

    private PtContract validateContract(Long ptContractId) {
        PtContract contract = ptContractRepository.findByIdWithMemberAndTrainer(ptContractId)
                .orElseThrow(() -> new IllegalArgumentException("PT 계약을 찾을 수 없습니다."));

        if (contract.getRemainingCount() <= 0) {
            throw new IllegalArgumentException("남은 PT 횟수가 없습니다.");
        }

        if (!contract.isActive()) {
            throw new IllegalArgumentException("비활성화된 PT 계약입니다.");
        }

        return contract;
    }

    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }

    private void validateScheduleModification(PtSchedule schedule, LocalDateTime changeTime, boolean isCancel, Long memberId, Long trainerId) {
        // 공통 권한 검사
        validateScheduleAuthority(schedule, memberId, trainerId);

        // --- Start: Moved Checks from validateScheduleAuthority --- 
        // 스케줄 상태 검사
        if (schedule.getStatus() != PtScheduleStatus.SCHEDULED) {
            throw new IllegalArgumentException("이미 처리되었거나 예약되지 않은 스케줄은 변경/취소할 수 없습니다.");
        }

        // 시간 검증 (이미 시작된 스케줄)
        LocalDateTime scheduleStartTime = schedule.getStartTime();
        if (scheduleStartTime.isBefore(LocalDateTime.now())) {
             throw new IllegalArgumentException("이미 시작된 PT 일정은 변경/취소할 수 없습니다.");
        }
        // --- End: Moved Checks --- 

        // 제한 시간 검증
        Trainer trainer = schedule.getPtContract().getTrainer();
        long hoursUntilStart = java.time.Duration.between(changeTime, scheduleStartTime).toHours();
        int limitHours = isCancel ? trainer.getScheduleCancelLimitHours() : trainer.getScheduleChangeLimitHours();

        if (hoursUntilStart < limitHours) {
            String action = isCancel ? "취소" : "변경";
            throw new IllegalArgumentException(
                    String.format("%s는 PT 시작 %d시간 전까지만 가능합니다.",
                            action, limitHours)
            );
        }
    }

    private void validateNoShow(PtSchedule schedule, Long trainerId) {
        if (trainerId == null) {
            throw new IllegalArgumentException("트레이너 ID가 필요합니다.");
        }
         Trainer trainer = trainerRepository.findById(trainerId)
                 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));

        if (!schedule.getPtContract().getTrainer().equals(trainer)) {
            throw new IllegalArgumentException("해당 스케줄을 불참 처리할 권한이 없습니다.");
        }

        if (schedule.getStatus() != PtScheduleStatus.SCHEDULED) {
            throw new IllegalArgumentException("예약 상태가 아닌 스케줄은 불참 처리할 수 없습니다.");
        }

        if (schedule.getStartTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("아직 시작되지 않은 스케줄은 불참 처리할 수 없습니다.");
        }
    }

    @Transactional
    public Long markAsNoShow(Long scheduleId, String reason, Long trainerId) {
        PtSchedule schedule = ptScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스케줄입니다."));
        validateNoShow(schedule, trainerId);

        schedule.setStatus(PtScheduleStatus.NO_SHOW);
        schedule.setReason(reason);
        schedule.setIsDeducted(true); // 불참 시 횟수 차감 유지

        return ptScheduleRepository.save(schedule).getId();
    }
    
    /**
     * 트레이너 ID와 회원 이름으로 해당 회원의 남은 PT 일정을 조회합니다.
     * 현재 시점 이후의 SCHEDULED 상태인 PT 일정만 반환합니다.
     *
     * @param trainerId 트레이너 ID
     * @param memberName 회원 이름
     * @return 남은 PT 일정 목록
     */
    @Transactional(readOnly = true)
    public List<PtScheduleResponseDTO> getRemainingSchedulesByTrainerIdAndMemberName(Long trainerId, String memberName) {
        if (trainerId == null) {
            throw new IllegalArgumentException("트레이너 ID는 필수입니다.");
        }
        
        if (memberName == null || memberName.isBlank()) {
            throw new IllegalArgumentException("회원 이름은 필수입니다.");
        }
        
        // 1. 트레이너 존재 여부 확인
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다."));
        
        // 2. 해당 이름을 가진 회원들 조회 (동명이인 가능성 고려)
        List<Member> members = memberRepository.findByNameContaining(memberName);
        
        if (members.isEmpty()) {
            return List.of(); // 해당 이름의 회원이 없으면 빈 리스트 반환
        }
        
        // 3. 각 회원 ID 추출
        List<Long> memberIds = members.stream()
                .map(Member::getId)
                .collect(Collectors.toList());
        
        // 4. 현재 시간 이후, 해당 트레이너와 회원들 간의 예약된(SCHEDULED) PT 일정 조회
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneYearLater = now.plusYears(1);
        
        List<PtSchedule> schedules = new ArrayList<>();
        
        for (Long memberId : memberIds) {
            // 해당 트레이너와 회원 간의 계약이 존재하는지 확인
            if (ptContractRepository.existsByMemberIdAndTrainerId(memberId, trainerId)) {
                List<Object[]> results = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_IdWithPtLog(
                        now, oneYearLater, memberId, PtScheduleStatus.SCHEDULED
                );
                
                List<PtSchedule> memberSchedules = results.stream()
                        .map(result -> {
                            PtSchedule schedule = (PtSchedule) result[0];
                            schedule.setPtLogId((Long) result[1]);
                            
                            // 해당 계약이 요청한 트레이너의 것인지 확인
                            if (schedule.getPtContract().getTrainer().getId().equals(trainerId)) {
                                return schedule;
                            }
                            return null;
                        })
                        .filter(schedule -> schedule != null)
                        .collect(Collectors.toList());
                
                schedules.addAll(memberSchedules);
            }
        }
        
        // 5. 시작 시간 기준 오름차순 정렬
        schedules.sort(Comparator.comparing(PtSchedule::getStartTime));
        
        // 6. DTO로 변환하여 반환
        return schedules.stream()
                .map(PtScheduleResponseDTO::from)
                .collect(Collectors.toList());
    }

}