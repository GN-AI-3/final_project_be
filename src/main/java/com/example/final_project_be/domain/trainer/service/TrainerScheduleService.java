package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.domain.trainer.dto.*;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.entity.TrainerUnavailableTime;
import com.example.final_project_be.domain.trainer.entity.TrainerWorkingTime;
import com.example.final_project_be.domain.trainer.enums.DayOfWeek;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerUnavailableTimeRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerWorkingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TrainerScheduleService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final TrainerRepository trainerRepository;
    private final TrainerWorkingTimeRepository trainerWorkingTimeRepository;
    private final TrainerUnavailableTimeRepository trainerUnavailableTimeRepository;
    private final PtScheduleRepository ptScheduleRepository;

    /**
     * 트레이너의 근무 시간을 업데이트합니다.
     *
     * @param trainerId 트레이너 ID
     * @param requests  업데이트할 근무 시간 목록
     * @return 업데이트 결과
     */
    public TrainerWorkingTimeUpdateResponseDTO updateWorkingTime(Long trainerId, List<TrainerWorkingTimeUpdateRequestDTO> requests) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("트레이너를 찾을 수 없습니다."));

        List<DayOfWeek> updatedDays = requests.stream()
                .map(request -> {
                    TrainerWorkingTime workingTime = trainerWorkingTimeRepository.findByTrainerIdAndDay(trainerId, request.getDay())
                            .orElseGet(() -> {
                                TrainerWorkingTime newWorkingTime = new TrainerWorkingTime();
                                newWorkingTime.setTrainer(trainer);
                                newWorkingTime.setDay(request.getDay());
                                return newWorkingTime;
                            });

                    workingTime.setStartTime(LocalTime.parse(request.getStartTime(), TIME_FORMATTER));
                    workingTime.setEndTime(LocalTime.parse(request.getEndTime(), TIME_FORMATTER));
                    workingTime.setIsActive(request.getIsActive());

                    trainerWorkingTimeRepository.save(workingTime);
                    return request.getDay();
                })
                .collect(Collectors.toList());

        return TrainerWorkingTimeUpdateResponseDTO.builder()
                .trainerId(trainerId)
                .updatedDays(updatedDays)
                .message("근무 시간 업데이트 성공")
                .build();
    }

    /**
     * 트레이너의 불가능한 시간을 등록합니다.
     *
     * @param trainerId 트레이너 ID
     * @param request   등록할 불가능한 시간 정보
     * @return 등록된 불가능한 시간 정보
     */
    public TrainerUnavailableTimeResponseDTO createUnavailableTime(Long trainerId, TrainerUnavailableTimeCreateRequestDTO request) {
        Trainer trainer = trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("트레이너를 찾을 수 없습니다."));

        LocalDateTime startTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(request.getStartTime()),
                ZoneId.systemDefault()
        );

        LocalDateTime endTime = LocalDateTime.ofInstant(
                Instant.ofEpochSecond(request.getEndTime()),
                ZoneId.systemDefault()
        );

        TrainerUnavailableTime unavailableTime = new TrainerUnavailableTime();
        unavailableTime.setTrainer(trainer);
        unavailableTime.setStartTime(startTime);
        unavailableTime.setEndTime(endTime);
        unavailableTime.setReason(request.getReason());

        TrainerUnavailableTime savedUnavailableTime = trainerUnavailableTimeRepository.save(unavailableTime);

        return TrainerUnavailableTimeResponseDTO.builder()
                .id(savedUnavailableTime.getId())
                .trainerId(trainerId)
                .startTime(savedUnavailableTime.getStartTime())
                .endTime(savedUnavailableTime.getEndTime())
                .reason(savedUnavailableTime.getReason())
                .createdAt(savedUnavailableTime.getCreatedAt())
                .build();
    }

    private boolean isAvailableTime(TrainerWorkingTime workingTime,
                                    List<TrainerUnavailableTime> unavailableTimes,
                                    List<PtSchedule> ptSchedules,
                                    LocalDateTime slotStartTime,
                                    LocalDateTime slotEndTime) {
        // 1. 근무 시간 체크
        if (workingTime == null || !workingTime.getIsActive()) {
            return false;
        }

        LocalTime localSlotStartTime = slotStartTime.toLocalTime();
        LocalTime localSlotEndTime = slotEndTime.toLocalTime();
        boolean isWorkingTime = !localSlotStartTime.isBefore(workingTime.getStartTime())
                && !localSlotEndTime.isAfter(workingTime.getEndTime());

        if (!isWorkingTime) {
            return false;
        }

        // 2. 불가능한 시간 체크
        boolean isUnavailableTime = unavailableTimes.stream()
                .anyMatch(unavailableTime ->
                        (!slotStartTime.isBefore(unavailableTime.getStartTime()) && slotStartTime.isBefore(unavailableTime.getEndTime()))
                                || (!slotEndTime.isBefore(unavailableTime.getStartTime()) && slotEndTime.isBefore(unavailableTime.getEndTime()))
                );

        if (isUnavailableTime) {
            return false;
        }

        // 3. 예약된 스케줄 체크
        boolean isScheduledTime = ptSchedules.stream()
                .anyMatch(schedule ->
                        (!slotStartTime.isBefore(schedule.getStartTime()) && slotStartTime.isBefore(schedule.getEndTime()))
                                || (!slotEndTime.isBefore(schedule.getStartTime()) && slotEndTime.isBefore(schedule.getEndTime())));

        return !isScheduledTime;
    }

    public TrainerAvailableTimesResponseDTO getAvailableTimes(Long trainerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer sessionMinutes) {
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("트레이너를 찾을 수 없습니다."));

        List<TrainerWorkingTime> workingTimes = trainerWorkingTimeRepository.findByTrainerId(trainerId);
        List<TrainerUnavailableTime> unavailableTimes = trainerUnavailableTimeRepository.findByTrainerIdAndStartTimeBetween(
                trainerId, startDateTime, endDateTime);
        List<PtSchedule> ptSchedules = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(
                startDateTime, endDateTime, trainerId, PtScheduleStatus.SCHEDULED);

        // 요일별 근무시간 Map 생성
        Map<DayOfWeek, TrainerWorkingTime> workingTimeMap = workingTimes.stream()
                .collect(Collectors.toMap(
                        TrainerWorkingTime::getDay,
                        workingTime -> workingTime,
                        (existing, replacement) -> existing
                ));

        List<TrainerAvailableTimesResponseDTO.AvailableTimeSlot> availableSlots = new ArrayList<>();
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());

        LocalDateTime currentDate = startDateTime;
        while (!currentDate.isAfter(endDateTime)) {
            // 1. 현재 날짜의 요일과 해당 요일의 근무시간 조회
            DayOfWeek dayOfWeek = DayOfWeek.values()[currentDate.getDayOfWeek().getValue() - 1];
            TrainerWorkingTime workingTime = workingTimeMap.get(dayOfWeek);

            if (workingTime != null && workingTime.getIsActive()) {
                // 2. 해당 날짜의 근무 시작/종료 시간 설정
                LocalDateTime dayStartTime = LocalDateTime.of(currentDate.toLocalDate(), workingTime.getStartTime());
                LocalDateTime dayEndTime = LocalDateTime.of(currentDate.toLocalDate(), workingTime.getEndTime());

                // 3. 요청된 시작 시간이 근무 시작 시간보다 이르면 근무 시작 시간으로 조정
                if (startDateTime.isBefore(dayStartTime)) {
                    currentDate = dayStartTime;
                }

                // 4. 요청된 시작 시간이 근무 종료 시간보다 늦은 경우 다음날로 이동
                else if (startDateTime.isAfter(dayEndTime)) {
                    currentDate = currentDate.plusDays(1);
                    continue;
                }

                // 5. 시작 시간을 정각 또는 30분으로 조정
                LocalDateTime slotStartTime = startDateTime;
                int minutes = slotStartTime.getMinute();
                if (minutes > 0 && minutes < 30) {
                    slotStartTime = slotStartTime.withMinute(30).withSecond(0).withNano(0);
                } else if (minutes > 30) {
                    slotStartTime = slotStartTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
                } else {
                    slotStartTime = slotStartTime.withSecond(0).withNano(0);
                }

                LocalDateTime slotEndTime = slotStartTime.plusMinutes(sessionMinutes);

                // 6. 해당 날짜의 근무 시간 내에서 30분 단위로 슬롯 생성
                while (!slotEndTime.isAfter(dayEndTime)) {
                    // 6-1. 해당 슬롯이 예약 가능한지 체크
                    if (isAvailableTime(workingTime, unavailableTimes, ptSchedules, slotStartTime, slotEndTime)) {
                        availableSlots.add(TrainerAvailableTimesResponseDTO.AvailableTimeSlot.builder()
                                .startTime(slotStartTime.toEpochSecond(zoneOffset))
                                .endTime(slotEndTime.toEpochSecond(zoneOffset))
                                .build());
                    }

                    // 6-2. 30분 단위로 슬롯 이동
                    slotStartTime = slotStartTime.plusMinutes(30);
                    slotEndTime = slotEndTime.plusMinutes(30);
                }
            }

            // 7. 다음 날로 이동
            currentDate = currentDate.plusDays(1);
        }

        return TrainerAvailableTimesResponseDTO.builder()
                .trainerId(trainerId)
                .startTime(startDateTime.toEpochSecond(zoneOffset))
                .endTime(endDateTime.toEpochSecond(zoneOffset))
                .sessionMinutes(sessionMinutes)
                .availableTimes(availableSlots)
                .build();
    }

    /**
     * 트레이너의 근무 시간을 조회합니다.
     *
     * @param trainerId 트레이너 ID
     * @return 근무 시간 목록
     */
    @Transactional(readOnly = true)
    public List<TrainerWorkingTimeResponseDTO> getWorkingTimes(Long trainerId) {
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("트레이너를 찾을 수 없습니다."));

        List<TrainerWorkingTime> workingTimes = trainerWorkingTimeRepository.findByTrainerId(trainerId);

        return workingTimes.stream()
                .map(workingTime -> TrainerWorkingTimeResponseDTO.builder()
                        .day(workingTime.getDay())
                        .startTime(workingTime.getStartTime() != null ? workingTime.getStartTime().format(TIME_FORMATTER) : null)
                        .endTime(workingTime.getEndTime() != null ? workingTime.getEndTime().format(TIME_FORMATTER) : null)
                        .isActive(workingTime.getIsActive())
                        .build())
                .collect(Collectors.toList());
    }
} 