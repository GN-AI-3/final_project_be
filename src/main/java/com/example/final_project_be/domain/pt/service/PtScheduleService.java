package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.pt.dto.PtScheduleMemberResponseDTO;
import com.example.final_project_be.domain.pt.dto.PtScheduleTrainerResponseDTO;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.security.TrainerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;

    public <T> List<T> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate, PtScheduleStatus status, Object user) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate != null ? endDate.plusDays(1).atStartOfDay() : LocalDateTime.MAX;

        List<PtSchedule> schedules = status != null ?
                ptScheduleRepository.findByStartTimeBetweenAndStatus(startDateTime, endDateTime, status) :
                ptScheduleRepository.findByStartTimeBetween(startDateTime, endDateTime);

        return convertToResponseDTO(schedules, user);
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> convertToResponseDTO(List<PtSchedule> schedules, Object user) {
        if (user instanceof MemberDTO) {
            return (List<T>) schedules.stream()
                    .map(PtScheduleMemberResponseDTO::from)
                    .collect(Collectors.toList());
        } else if (user instanceof TrainerDTO) {
            return (List<T>) schedules.stream()
                    .map(PtScheduleTrainerResponseDTO::from)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }
} 