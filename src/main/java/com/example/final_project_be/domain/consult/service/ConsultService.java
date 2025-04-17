package com.example.final_project_be.domain.consult.service;

import com.example.final_project_be.domain.consult.dto.ConsultRequestDTO;
import com.example.final_project_be.domain.consult.dto.ConsultResponseDTO;

import java.util.List;

public interface ConsultService {
    
    /**
     * 새로운 상담 정보를 등록합니다.
     * 
     * @param requestDTO 상담 정보 등록 요청 DTO
     * @return 등록된 상담 정보 응답 DTO
     */
    ConsultResponseDTO createConsult(ConsultRequestDTO requestDTO);
    
    /**
     * 상담 ID로 상담 정보를 조회합니다.
     * 
     * @param consultId 상담 ID
     * @return 상담 정보 응답 DTO
     */
    ConsultResponseDTO getConsultById(Long consultId);
    
    /**
     * PT 계약 ID로 상담 정보를 조회합니다.
     * 
     * @param ptContractId PT 계약 ID
     * @return 상담 정보 응답 DTO
     */
    ConsultResponseDTO getConsultByPtContractId(Long ptContractId);
    
    /**
     * 트레이너 ID로 해당 트레이너가 진행한 모든 상담 정보를 조회합니다.
     * 
     * @param trainerId 트레이너 ID
     * @return 트레이너가 진행한 상담 목록
     */
    List<ConsultResponseDTO> getConsultsByTrainerId(Long trainerId);
    
    /**
     * 회원 ID로 해당 회원의 모든 상담 정보를 조회합니다.
     * 
     * @param memberId 회원 ID
     * @return 회원의 상담 목록
     */
    List<ConsultResponseDTO> getConsultsByMemberId(Long memberId);
} 