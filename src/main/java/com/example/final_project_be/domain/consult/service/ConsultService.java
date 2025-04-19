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
     * 회원 ID로 해당 회원의 상담 일지를 조회합니다.
     * 회원이 자신의 상담 일지를 조회하거나 트레이너가 담당 회원의 상담 일지를 조회할 때 사용합니다.
     *
     * @param memberId 회원 ID
     * @return 회원의 상담 일지 목록
     */
    List<ConsultResponseDTO> getConsultsByMemberId(Long memberId);
} 