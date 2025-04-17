package com.example.final_project_be.domain.consult.dto;

import com.example.final_project_be.domain.consult.entity.Consult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultResponseDTO {
    
    // ID 정보
    private Long id;
    private Long ptContractId;
    private Long memberId;
    private Long trainerId;
    private String memberName;
    private String trainerName;
    
    // 기본 정보
    private String job;
    private String lifestyle;
    private String medicalHistory;
    private String trainingGoal;
    private LocalDate goalDeadline;
    private LocalDate consultationDate;
    private String consultationChannel;
    private String consultantName;
    private String consultationNotes;
    
    // 운동 관련 정보
    private Boolean hasExperience;
    private String exerciseExperience;
    private Integer weeklyWorkoutFrequency;
    private List<String> preferredExercises;
    private List<String> dislikedExercises;
    private String weakPointsOrPain;
    private List<String> bodyConcerns;
    
    // 식단 정보
    private Boolean needsDietPlan;
    
    // 일정 정보
    private List<String> preferredDays;
    private List<String> preferredTimes;
    private Integer availableSessionsPerWeek;
    private String distanceToGym;
    
    // 생성, 수정 정보
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Consult 엔티티를 ConsultResponseDTO로 변환합니다.
     * 
     * @param consult 상담 엔티티
     * @return 변환된 DTO
     */
    public static ConsultResponseDTO from(Consult consult) {
        if (consult == null) {
            return null;
        }
        
        return ConsultResponseDTO.builder()
                .id(consult.getId())
                .ptContractId(consult.getPtContract().getId())
                .memberId(consult.getPtContract().getMember().getId())
                .trainerId(consult.getPtContract().getTrainer().getId())
                .memberName(consult.getPtContract().getMember().getName())
                .trainerName(consult.getPtContract().getTrainer().getName())
                
                // 기본 정보
                .job(consult.getJob())
                .lifestyle(consult.getLifestyle())
                .medicalHistory(consult.getMedicalHistory())
                .trainingGoal(consult.getTrainingGoal())
                .goalDeadline(consult.getGoalDeadline())
                .consultationDate(consult.getConsultationDate())
                .consultationChannel(consult.getConsultationChannel())
                .consultantName(consult.getConsultantName())
                .consultationNotes(consult.getConsultationNotes())
                
                // 운동 관련 정보
                .hasExperience(consult.getHasExperience())
                .exerciseExperience(consult.getExerciseExperience())
                .weeklyWorkoutFrequency(consult.getWeeklyWorkoutFrequency())
                .preferredExercises(consult.getPreferredExercises())
                .dislikedExercises(consult.getDislikedExercises())
                .weakPointsOrPain(consult.getWeakPointsOrPain())
                .bodyConcerns(consult.getBodyConcerns())
                
                // 식단 정보
                .needsDietPlan(consult.getNeedsDietPlan())
                
                // 일정 정보
                .preferredDays(consult.getPreferredDays())
                .preferredTimes(consult.getPreferredTimes())
                .availableSessionsPerWeek(consult.getAvailableSessionsPerWeek())
                .distanceToGym(consult.getDistanceToGym())
                
                // 생성, 수정 정보
                .createdAt(consult.getCreatedAt())
                .updatedAt(consult.getModifiedAt())
                .build();
    }
} 