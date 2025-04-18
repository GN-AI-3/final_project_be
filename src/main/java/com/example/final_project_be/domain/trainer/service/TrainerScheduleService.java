package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.trainer.dto.TrainerUnavailableTimeCreateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerUnavailableTimeResponseDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateResponseDTO;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.entity.TrainerUnavailableTime;
import com.example.final_project_be.domain.trainer.entity.TrainerWorkingTime;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerUnavailableTimeRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerWorkingTimeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

        List<Integer> updatedDays = requests.stream()
                .map(request -> {
                    TrainerWorkingTime workingTime = trainerWorkingTimeRepository.findByTrainerIdAndDayOfWeek(trainerId, request.getDayOfWeek())
                            .orElseGet(() -> {
                                TrainerWorkingTime newWorkingTime = new TrainerWorkingTime();
                                newWorkingTime.setTrainer(trainer);
                                newWorkingTime.setDayOfWeek(request.getDayOfWeek());
                                return newWorkingTime;
                            });

                    workingTime.setStartTime(LocalTime.parse(request.getStartTime(), TIME_FORMATTER));
                    workingTime.setEndTime(LocalTime.parse(request.getEndTime(), TIME_FORMATTER));
                    workingTime.setIsActive(request.getIsActive());

                    trainerWorkingTimeRepository.save(workingTime);
                    return request.getDayOfWeek();
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

        TrainerUnavailableTime unavailableTime = new TrainerUnavailableTime();
        unavailableTime.setTrainer(trainer);
        unavailableTime.setStartTime(request.getStartTime());
        unavailableTime.setEndTime(request.getEndTime());
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
} 