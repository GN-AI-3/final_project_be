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

    private boolean isAvailableTime(List<TrainerWorkingTime> workingTimes,
                                    List<TrainerUnavailableTime> unavailableTimes,
                                    List<PtSchedule> ptSchedules,
                                    LocalDateTime time,
                                    LocalDateTime endTime) {
        // 1. 근무 시간 체크
        boolean isWorkingTime = workingTimes.stream()
                .filter(workingTime -> workingTime.getDay() == DayOfWeek.values()[time.getDayOfWeek().getValue() - 1])
                .anyMatch(workingTime -> {
                    LocalTime localTime = time.toLocalTime();
                    return localTime.isAfter(workingTime.getStartTime()) &&
                            localTime.isBefore(workingTime.getEndTime());
                });

        if (!isWorkingTime) {
            return false;
        }

        // 2. 불가능한 시간 체크
        boolean isNotUnavailableTime = unavailableTimes.stream()
                .noneMatch(unavailableTime ->
                        (time.isAfter(unavailableTime.getStartTime()) &&
                                time.isBefore(unavailableTime.getEndTime())) ||
                                (endTime.isAfter(unavailableTime.getStartTime()) &&
                                        endTime.isBefore(unavailableTime.getEndTime())));

        if (!isNotUnavailableTime) {
            return false;
        }

        // 3. 예약된 스케줄 체크
        return ptSchedules.stream()
                .noneMatch(schedule ->
                        (time.isAfter(schedule.getStartTime()) &&
                                time.isBefore(schedule.getEndTime())) ||
                                (endTime.isAfter(schedule.getStartTime()) &&
                                        endTime.isBefore(schedule.getEndTime())));
    }

    public TrainerAvailableTimesResponseDTO getAvailableTimes(Long trainerId, LocalDateTime startDateTime, LocalDateTime endDateTime, Integer sessionMinutes) {
        trainerRepository.findById(trainerId)
                .orElseThrow(() -> new RuntimeException("트레이너를 찾을 수 없습니다."));

        List<TrainerWorkingTime> workingTimes = trainerWorkingTimeRepository.findByTrainerId(trainerId);
        List<TrainerUnavailableTime> unavailableTimes = trainerUnavailableTimeRepository.findByTrainerIdAndStartTimeBetween(
                trainerId, startDateTime, endDateTime);
        List<PtSchedule> ptSchedules = ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(
                startDateTime, endDateTime, trainerId, PtScheduleStatus.SCHEDULED);

        List<TrainerAvailableTimesResponseDTO.AvailableTimeSlot> availableSlots = new ArrayList<>();
        ZoneOffset zoneOffset = ZoneId.systemDefault().getRules().getOffset(Instant.now());

        // 시작 시간을 정각 또는 30분으로 조정
        LocalDateTime currentTime = startDateTime;
        int minutes = currentTime.getMinute();
        if (minutes > 0 && minutes < 30) {
            currentTime = currentTime.withMinute(30).withSecond(0).withNano(0);
        } else if (minutes > 30) {
            currentTime = currentTime.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        } else {
            currentTime = currentTime.withSecond(0).withNano(0);
        }

        while (currentTime.isBefore(endDateTime)) {
            LocalDateTime slotEndTime = currentTime.plusMinutes(sessionMinutes);

            if (isAvailableTime(workingTimes, unavailableTimes, ptSchedules, currentTime, slotEndTime)) {
                availableSlots.add(TrainerAvailableTimesResponseDTO.AvailableTimeSlot.builder()
                        .startTime(currentTime.toEpochSecond(zoneOffset))
                        .endTime(slotEndTime.toEpochSecond(zoneOffset))
                        .build());
            }

            // 30분 단위로 증가
            currentTime = currentTime.plusMinutes(30);
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
        Trainer trainer = trainerRepository.findById(trainerId)
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