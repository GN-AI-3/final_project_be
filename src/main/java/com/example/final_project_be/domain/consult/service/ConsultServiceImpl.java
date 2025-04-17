package com.example.final_project_be.domain.consult.service;

import com.example.final_project_be.domain.consult.dto.ConsultRequestDTO;
import com.example.final_project_be.domain.consult.dto.ConsultResponseDTO;
import com.example.final_project_be.domain.consult.entity.Consult;
import com.example.final_project_be.domain.consult.repository.ConsultRepository;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsultServiceImpl implements ConsultService {

    private final ConsultRepository consultRepository;
    private final PtContractRepository ptContractRepository;
    
    @Override
    @Transactional
    public ConsultResponseDTO createConsult(ConsultRequestDTO requestDTO) {
        log.info("Creating new consultation for PT contract ID: {}", requestDTO.getPtContractId());
        
        if (requestDTO.getPtContractId() == null) {
            throw new IllegalArgumentException("PT 계약 ID는 필수 입력값입니다.");
        }
        
        // 1. PT 계약 존재 여부 확인
        PtContract ptContract = ptContractRepository.findById(requestDTO.getPtContractId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 PT 계약입니다. ID: " + requestDTO.getPtContractId()));
        
        // 2. 이미 상담 정보가 있는지 확인
        if (consultRepository.existsByPtContractId(ptContract.getId())) {
            throw new IllegalStateException("이미 상담 정보가 등록되어 있습니다. PT 계약 ID: " + ptContract.getId());
        }
        
        // 3. Consult 엔티티 생성
        Consult consult = buildConsultEntity(ptContract, requestDTO);
        
        // 4. 저장
        Consult savedConsult = consultRepository.save(consult);
        log.info("Successfully created consultation with ID: {}", savedConsult.getId());
        
        // 5. 응답 DTO 변환
        return ConsultResponseDTO.from(savedConsult);
    }
    
    @Override
    public ConsultResponseDTO getConsultById(Long consultId) {
        log.info("Fetching consultation with ID: {}", consultId);
        
        Consult consult = findConsultByIdOrThrow(consultId);
        
        return ConsultResponseDTO.from(consult);
    }
    
    @Override
    public ConsultResponseDTO getConsultByPtContractId(Long ptContractId) {
        log.info("Fetching consultation by PT contract ID: {}", ptContractId);
        
        Consult consult = consultRepository.findByPtContractId(ptContractId)
                .orElseThrow(() -> new IllegalArgumentException("해당 PT 계약의 상담 정보가 없습니다. 계약 ID: " + ptContractId));
        
        return ConsultResponseDTO.from(consult);
    }
    
    @Override
    public List<ConsultResponseDTO> getConsultsByTrainerId(Long trainerId) {
        log.info("Fetching consultations by trainer ID: {}", trainerId);
        
        List<Consult> consults = consultRepository.findByTrainerId(trainerId);
        
        return consults.stream()
                .map(ConsultResponseDTO::from)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ConsultResponseDTO> getConsultsByMemberId(Long memberId) {
        log.info("Fetching consultations by member ID: {}", memberId);
        
        List<Consult> consults = consultRepository.findByMemberId(memberId);
        
        return consults.stream()
                .map(ConsultResponseDTO::from)
                .collect(Collectors.toList());
    }
    
    /**
     * ConsultRequestDTO로부터 Consult 엔티티를 생성합니다.
     */
    private Consult buildConsultEntity(PtContract ptContract, ConsultRequestDTO dto) {
        return Consult.builder()
                .ptContract(ptContract)
                
                // 1. 기본 정보
                .job(dto.getJob())
                .lifestyle(dto.getLifestyle())
                .medicalHistory(dto.getMedicalHistory())
                .trainingGoal(dto.getTrainingGoal())
                .goalDeadline(dto.getGoalDeadline())
                .consultationDate(dto.getConsultationDate())
                .consultationChannel(dto.getConsultationChannel())
                .consultantName(dto.getConsultantName() != null ? dto.getConsultantName() : ptContract.getTrainer().getName())
                .consultationNotes(dto.getConsultationNotes())
                
                // 2. 운동 정보
                .hasExperience(dto.getHasExperience())
                .exerciseExperience(dto.getExerciseExperience())
                .weeklyWorkoutFrequency(dto.getWeeklyWorkoutFrequency())
                .preferredExercises(dto.getPreferredExercises() != null ? dto.getPreferredExercises() : Collections.emptyList())
                .dislikedExercises(dto.getDislikedExercises() != null ? dto.getDislikedExercises() : Collections.emptyList())
                .weakPointsOrPain(dto.getWeakPointsOrPain())
                .bodyConcerns(dto.getBodyConcerns() != null ? dto.getBodyConcerns() : Collections.emptyList())
                
                // 3. 식단 정보
                .needsDietPlan(dto.getNeedsDietPlan())
                
                // 4. 일정 정보
                .preferredDays(dto.getPreferredDays() != null ? dto.getPreferredDays() : Collections.emptyList())
                .preferredTimes(dto.getPreferredTimes() != null ? dto.getPreferredTimes() : Collections.emptyList())
                .availableSessionsPerWeek(dto.getAvailableSessionsPerWeek())
                .distanceToGym(dto.getDistanceToGym())
                .build();
    }
    
    /**
     * 상담 ID로 상담 정보를 조회하며, 없으면 예외를 발생시킵니다.
     */
    private Consult findConsultByIdOrThrow(Long consultId) {
        if (consultId == null) {
            throw new IllegalArgumentException("상담 ID는 필수 입력값입니다.");
        }
        
        return consultRepository.findById(consultId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상담 정보입니다. ID: " + consultId));
    }
} 