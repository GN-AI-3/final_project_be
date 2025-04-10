package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.service.MemberService;
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
            schedules = status != null ?
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_IdAndStatus(
                            startTime, endTime, member.getId(), status) :
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_Id(
                            startTime, endTime, member.getId());
        } else if (user instanceof TrainerDTO trainer) {
            schedules = status != null ?
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(
                            startTime, endTime, trainer.getId(), status) :
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_Id(
                            startTime, endTime, trainer.getId());
        } else {
            throw new IllegalArgumentException("유효하지 않은 사용자 타입입니다.");
        }

        return convertToResponseDTO(schedules);
    }

    private List<PtScheduleResponseDTO> convertToResponseDTO(List<PtSchedule> schedules) {
        return schedules.stream()
                .map(schedule -> {
                    PtContract contract = schedule.getPtContract();
                    return PtScheduleResponseDTO.builder()
                            .id(schedule.getId())
                            .ptContractId(contract.getId())
                            .startTime(schedule.getStartTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                            .endTime(schedule.getEndTime().atZone(ZoneId.systemDefault()).toEpochSecond())
                            .status(schedule.getStatus())
                            .reservationId(schedule.getReservationId())
                            .trainerId(contract.getTrainer().getId())
                            .trainerName(contract.getTrainer().getName())
                            .memberId(contract.getMember().getId())
                            .memberName(contract.getMember().getName())
                            .currentPtCount(schedule.getCurrentPtCount())
                            .totalCount(contract.getTotalCount())
                            .remainingPtCount(contract.getRemainingCount())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createSchedule(PtScheduleCreateRequestDTO request, MemberDTO member) {
        // 계약 유효성 검사
        PtContract contract = validateContract(request.getPtContractId());

        // 요청한 회원이 계약의 실제 회원인지 확인
        MemberDetailDTO memberDetail = memberService.getMemberInfo(contract.getMember().getId());
        if (!memberDetail.getEmail().equals(member.getEmail())) {
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
                .build();

        return ptScheduleRepository.save(ptSchedule).getId();
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
        List<PtSchedule> existingSchedules = ptScheduleRepository.findByPtContractIdAndStatus(
                ptContractId, PtScheduleStatus.SCHEDULED);

        for (PtSchedule schedule : existingSchedules) {
            if ((startTime.isBefore(schedule.getEndTime()) && endTime.isAfter(schedule.getStartTime()))) {
                throw new IllegalArgumentException("이미 예약된 시간과 중복됩니다.");
            }
        }
    }

    private LocalDateTime convertToLocalDateTime(Long timestamp) {
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }
} 