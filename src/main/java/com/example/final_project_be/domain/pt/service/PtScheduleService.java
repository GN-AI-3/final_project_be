package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.service.MemberService;
import com.example.final_project_be.domain.pt.dto.PtScheduleChangeRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleChangeResponseDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleCreateRequestDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleResponseDTO;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.security.TrainerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;
    private final PtContractRepository ptContractRepository;
    private final MemberService memberService;

    @Transactional(readOnly = true)
    public List<PtScheduleResponseDTO> getSchedulesByDateRange(
            @NonNull LocalDateTime startTime,
            @NonNull LocalDateTime endTime,
            PtScheduleStatus status,
            Object user) {

        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }

        List<PtSchedule> schedules;
        if (user instanceof MemberDTO member) {
            schedules = ptScheduleRepository.findSchedulesForCountCalculationByMember(
                    startTime, endTime, status, member.getId()
            );
        } else if (user instanceof TrainerDTO trainer) {
            schedules = ptScheduleRepository.findSchedulesForCountCalculationByTrainer(
                    startTime, endTime, status, trainer.getId()
            );
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다.");
        }

        return schedules.stream()
                .filter(schedule ->
                        schedule.getStartTime().isAfter(startTime.minusSeconds(1)) &&
                                schedule.getStartTime().isBefore(endTime.plusSeconds(1)) &&
                                (status == null || schedule.getStatus() == status)
                )
                .map(PtScheduleResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSchedule(PtScheduleCreateRequestDTO request, Object user, boolean shouldCheckRemaining) {
        // 계약 유효성 검사
        PtContract contract = validateContract(request.getPtContractId());

        if (shouldCheckRemaining) {
            // 남은 횟수 체크
            if (contract.getRemainingCount() <= 0) {
                throw new IllegalArgumentException("남은 PT 횟수가 없습니다.");
            }
            // 횟수 차감
            contract.setUsedCount(contract.getUsedCount() + 1);
            ptContractRepository.save(contract);
        }

        // 요청한 회원이 계약의 실제 회원인지 확인
        MemberDetailDTO memberDetail = memberService.getMemberInfo(contract.getMember().getId());
        if (user instanceof MemberDTO member && !memberDetail.getEmail().equals(member.getEmail())) {
            throw new IllegalArgumentException("해당 PT 계약에 대한 권한이 없습니다.");
        }

        // 시간 유효성 검사
        LocalDateTime startTime = convertToLocalDateTime(request.getStartTime());
        LocalDateTime endTime = request.getEndTime() != null ?
                convertToLocalDateTime(request.getEndTime()) :
                startTime.plusHours(1);

        validateTime(startTime, endTime);

        // 중복 체크
        checkTimeOverlap(startTime, endTime, request.getPtContractId());

        // PT 스케줄 생성
        PtSchedule ptSchedule = PtSchedule.builder()
                .ptContract(contract)
                .startTime(startTime)
                .endTime(endTime)
                .status(PtScheduleStatus.SCHEDULED)
                .isDeducted(true)
                .build();

        // 회차 계산 및 저장
        recalculatePtCounts(contract, startTime);
        return ptScheduleRepository.save(ptSchedule).getId();
    }

    @Transactional
    public PtSchedule cancelSchedule(Long scheduleId, String reason, Object user) {
        PtSchedule schedule = getPtSchedule(scheduleId);
        validateScheduleAuthority(schedule, user);

        if (schedule.getStatus() != PtScheduleStatus.SCHEDULED) {
            throw new IllegalArgumentException("이미 취소되었거나 완료된 PT 일정입니다.");
        }

        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("이미 시작된 PT 일정은 취소할 수 없습니다.");
        }

        // 스케줄 취소
        schedule.setStatus(PtScheduleStatus.CANCELLED);
        schedule.setReason(reason);

        // 회차 정보 업데이트
        recalculatePtCounts(schedule.getPtContract(), schedule.getStartTime());

        return schedule;
    }

    @Transactional
    public PtScheduleChangeResponseDTO changeSchedule(Long scheduleId, PtScheduleChangeRequestDTO request, Object user) {
        // 1. 기존 일정 조회 및 권한 검증
        PtSchedule oldSchedule = getPtSchedule(scheduleId);
        validateScheduleAuthority(oldSchedule, user);

        // 2. 기존 일정 상태를 CHANGED로 변경
        oldSchedule.setStatus(PtScheduleStatus.CHANGED);
        oldSchedule.setReason(request.getReason());

        // 3. 새로운 일정 생성 (횟수 차감 체크 비활성화)
        PtScheduleCreateRequestDTO createRequest = new PtScheduleCreateRequestDTO();
        createRequest.setPtContractId(oldSchedule.getPtContract().getId());
        createRequest.setStartTime(request.getStartTime());
        createRequest.setEndTime(request.getEndTime());

        Long newScheduleId = createSchedule(createRequest, user, false);  // 일정 변경은 횟수 차감 없음
        PtSchedule newSchedule = getPtSchedule(newScheduleId);

        // 4. 회차 재계산 (이전 일정과 새로운 일정 중 더 이른 시간 기준)
        LocalDateTime recalculateTime = oldSchedule.getStartTime().isBefore(newSchedule.getStartTime())
                ? oldSchedule.getStartTime()
                : newSchedule.getStartTime();
        recalculatePtCounts(oldSchedule.getPtContract(), recalculateTime);

        return PtScheduleChangeResponseDTO.of(oldSchedule, newSchedule);
    }

    /**
     * PT 계약의 특정 시점 이후의 모든 스케줄 회차를 재계산합니다.
     *
     * @param contract  PT 계약
     * @param startTime 이 시점 이후의 스케줄들만 재계산
     */
    @Transactional
    public void recalculatePtCounts(PtContract contract, LocalDateTime startTime) {
        List<PtSchedule> schedules = ptScheduleRepository.findByPtContractIdAndStatus(
                        contract.getId(),
                        null  // 모든 상태의 스케줄 조회
                ).stream()
                .filter(s -> s.getStartTime().isAfter(startTime))
                .sorted(Comparator.comparing(PtSchedule::getStartTime))
                .collect(Collectors.toList());

        // 시작 시점 이전의 회차 계산
        int currentCount = calculatePreviousPtCount(contract, startTime);

        // 이후 스케줄들의 회차 업데이트
        for (PtSchedule schedule : schedules) {
            if (schedule.getIsDeducted()) {
                currentCount++;
                schedule.setCurrentPtCount(currentCount);
            } else {
                schedule.setCurrentPtCount(null);
            }
        }
    }

    /**
     * 특정 시점 이전까지의 PT 회차를 계산합니다.
     */
    private int calculatePreviousPtCount(PtContract contract, LocalDateTime beforeTime) {
        return (int) ptScheduleRepository.findByPtContractIdAndStatus(
                        contract.getId(),
                        null
                ).stream()
                .filter(s -> s.getStartTime().isBefore(beforeTime))
                .filter(PtSchedule::getIsDeducted)
                .count();
    }

    private void validateScheduleAuthority(PtSchedule schedule, Object user) {
        if (user instanceof MemberDTO member) {
            if (!schedule.getPtContract().getMember().getId().equals(member.getId())) {
                throw new IllegalArgumentException("해당 PT 일정에 대한 변경 권한이 없습니다.");
            }
        } else if (user instanceof TrainerDTO trainer) {
            if (!schedule.getPtContract().getTrainer().getId().equals(trainer.getId())) {
                throw new IllegalArgumentException("해당 PT 일정에 대한 변경 권한이 없습니다.");
            }
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다.");
        }

        if (schedule.getStatus() != PtScheduleStatus.SCHEDULED) {
            throw new IllegalArgumentException("이미 취소되었거나 완료된 PT 일정입니다.");
        }

        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("이미 시작된 PT 일정은 변경할 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public PtSchedule getPtSchedule(Long ptScheduleId) {
        return ptScheduleRepository.findByIdWithContractAndMembers(ptScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("PT 스케줄을 찾을 수 없습니다."));
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

    private void validateTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("시작 시간은 현재 시간보다 이후여야 합니다.");
        }
        if (endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("종료 시간은 시작 시간보다 이후여야 합니다.");
        }
    }

    private void checkTimeOverlap(LocalDateTime startTime, LocalDateTime endTime, Long ptContractId) {
        List<PtSchedule> existingSchedules = ptScheduleRepository.findOverlappingSchedules(
                ptContractId,
                startTime,
                endTime,
                PtScheduleStatus.SCHEDULED
        );

        if (!existingSchedules.isEmpty()) {
            throw new IllegalArgumentException("이미 예약된 시간과 중복됩니다.");
        }
    }

    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }
} 