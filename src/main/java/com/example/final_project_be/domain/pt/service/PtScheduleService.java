package com.example.final_project_be.domain.pt.service;

import com.example.final_project_be.domain.member.repository.MemberRepository;
import com.example.final_project_be.domain.pt.dto.PtScheduleResponseDTO;
import com.example.final_project_be.domain.pt.entity.PtSchedule;
import com.example.final_project_be.domain.pt.enums.PtScheduleStatus;
import com.example.final_project_be.domain.pt.repository.PtScheduleRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.security.TrainerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PtScheduleService {

    private final PtScheduleRepository ptScheduleRepository;
    private final MemberRepository memberRepository;
    private final TrainerRepository trainerRepository;

    public List<PtScheduleResponseDTO> getSchedulesByDateRange(LocalDateTime startTime, LocalDateTime endTime, PtScheduleStatus status, Object user) {
        LocalDateTime endDateTime = endTime != null ? endTime : LocalDateTime.MAX;
        List<PtSchedule> schedules;

        if (user instanceof MemberDTO member) {
            Long memberId = memberRepository.findByEmail(member.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Member not found"))
                    .getId();
            schedules = status != null ?
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_IdAndStatus(startTime, endDateTime, memberId, status) :
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Member_Id(startTime, endDateTime, memberId);
        } else if (user instanceof TrainerDTO trainer) {
            Long trainerId = trainerRepository.findByEmail(trainer.getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("Trainer not found"))
                    .getId();
            schedules = status != null ?
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_IdAndStatus(startTime, endDateTime, trainerId, status) :
                    ptScheduleRepository.findByStartTimeBetweenAndPtContract_Trainer_Id(startTime, endDateTime, trainerId);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        return convertToResponseDTO(schedules);
    }

    private List<PtScheduleResponseDTO> convertToResponseDTO(List<PtSchedule> schedules) {
        return schedules.stream()
                .map(PtScheduleResponseDTO::from)
                .collect(Collectors.toList());
    }
} 