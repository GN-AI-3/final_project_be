package com.example.final_project_be.domain.consult.service;

import com.example.final_project_be.domain.consult.dto.ConsultRequestDTO;
import com.example.final_project_be.domain.consult.dto.ConsultResponseDTO;
import com.example.final_project_be.domain.consult.entity.Consult;
import com.example.final_project_be.domain.consult.repository.ConsultRepository;
import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
@Transactional
public class ConsultServiceImpl implements ConsultService {

    private final ConsultRepository consultRepository;
    private final PtContractRepository ptContractRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
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
    
    /**
     * 회원 ID로 해당 회원의 상담 일지를 조회합니다.
     * 회원이 자신의 상담 일지를 조회할 때만 사용합니다.
     * 트레이너는 이 메서드를 직접 사용해서는 안 되며, getConsultsByPtContractId 메서드를 통해 조회해야 합니다.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ConsultResponseDTO> getConsultsByMemberId(Long memberId) {
        log.info("Fetching consultations for member ID: {}", memberId);
        
        List<Consult> consults = consultRepository.findByMemberIdWithFetchJoin(memberId);
        
        return consults.stream()
                .map(ConsultResponseDTO::from)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ConsultResponseDTO> getConsultsByPtContractId(Long ptContractId, Long trainerId) {
        log.info("Fetching consultations for PT contract ID: {} by trainer ID: {}", ptContractId, trainerId);
        
        // 1. PT 계약 존재 여부 확인 및 트레이너 권한 검증
        PtContract ptContract = ptContractRepository.findById(ptContractId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 PT 계약입니다. ID: " + ptContractId));
        
        // 2. 해당 PT 계약이 요청한 트레이너의 계약인지 확인
        if (!ptContract.getTrainer().getId().equals(trainerId)) {
            throw new IllegalArgumentException("해당 PT 계약에 대한 접근 권한이 없습니다.");
        }
        
        // 3. 회원 ID를 통해 상담 일지 조회
        Long memberId = ptContract.getMember().getId();
        List<Consult> consults = consultRepository.findByMemberIdWithFetchJoin(memberId);
        
        return consults.stream()
                .map(ConsultResponseDTO::from)
                .collect(Collectors.toList());
    }
    
    /**
     * ConsultRequestDTO로부터 Consult 엔티티를 생성합니다.
     */
    private Consult buildConsultEntity(PtContract ptContract, ConsultRequestDTO dto) {
        Consult consult = Consult.builder()
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
                .weakPointsOrPain(dto.getWeakPointsOrPain())
                
                // 3. 식단 정보
                .needsDietPlan(dto.getNeedsDietPlan())
                
                // 4. 일정 정보
                .availableSessionsPerWeek(dto.getAvailableSessionsPerWeek())
                .distanceToGym(dto.getDistanceToGym())
                .build();
        
        // List 타입 필드는 별도 설정
        if (dto.getPreferredExercises() != null) {
            consult.setPreferredExercises(dto.getPreferredExercises());
        }
        
        if (dto.getDislikedExercises() != null) {
            consult.setDislikedExercises(dto.getDislikedExercises());
        }
        
        if (dto.getBodyConcerns() != null) {
            consult.setBodyConcerns(dto.getBodyConcerns());
        }
        
        if (dto.getPreferredDays() != null) {
            consult.setPreferredDays(dto.getPreferredDays());
        }
        
        if (dto.getPreferredTimes() != null) {
            consult.setPreferredTimes(dto.getPreferredTimes());
        }
        
        return consult;
    }
} 