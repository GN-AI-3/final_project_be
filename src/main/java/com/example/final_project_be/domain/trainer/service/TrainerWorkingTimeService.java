package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerWorkingTimeUpdateResponseDTO;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.entity.TrainerWorkingTime;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
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
public class TrainerWorkingTimeService {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final TrainerRepository trainerRepository;
    private final TrainerWorkingTimeRepository trainerWorkingTimeRepository;

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
} 