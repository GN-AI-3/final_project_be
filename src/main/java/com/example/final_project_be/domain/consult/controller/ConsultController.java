package com.example.final_project_be.domain.consult.controller;

import com.example.final_project_be.domain.consult.dto.ConsultRequestDTO;
import com.example.final_project_be.domain.consult.dto.ConsultResponseDTO;
import com.example.final_project_be.domain.consult.entity.Consult;
import com.example.final_project_be.domain.consult.service.ConsultService;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consults")
@Tag(name = "상담 관리", description = "상담 정보 CRUD API")
public class ConsultController {

    private final ConsultService consultService;

    @PostMapping
    @PreAuthorize("hasAnyRole('TRAINER')")
    @Operation(summary = "상담 정보 등록", description = "새로운 상담 정보를 등록합니다.")
    public ResponseEntity<ConsultResponseDTO> createConsult(
            @Valid @RequestBody ConsultRequestDTO requestDTO,
            @AuthenticationPrincipal TrainerDTO trainer) {
        log.info("Trainer {} is creating a new consultation for PT contract ID: {}", 
                trainer.getEmail(), requestDTO.getPtContractId());
        ConsultResponseDTO responseDTO = consultService.createConsult(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER')")
    @Operation(summary = "상담 정보 조회", description = "상담 ID로 상담 정보를 조회합니다.")
    public ResponseEntity<ConsultResponseDTO> getConsultById(@PathVariable("id") Long consultId) {
        log.info("Fetching consultation with ID: {}", consultId);
        ConsultResponseDTO responseDTO = consultService.getConsultById(consultId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/contract/{ptContractId}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER')")
    @Operation(summary = "PT 계약별 상담 정보 조회", description = "PT 계약 ID로 상담 정보를 조회합니다.")
    public ResponseEntity<ConsultResponseDTO> getConsultByPtContractId(@PathVariable Long ptContractId) {
        log.info("Fetching consultation for PT contract ID: {}", ptContractId);
        ConsultResponseDTO responseDTO = consultService.getConsultByPtContractId(ptContractId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/trainer")
    @PreAuthorize("hasAnyRole('TRAINER')")
    @Operation(summary = "트레이너별 상담 정보 조회", description = "로그인한 트레이너의 상담 정보 목록을 조회합니다.")
    public ResponseEntity<List<ConsultResponseDTO>> getConsultsByTrainer(
            @AuthenticationPrincipal TrainerDTO trainer) {
        log.info("Fetching consultations for trainer: {}", trainer.getEmail());
        List<ConsultResponseDTO> responseDTOs = consultService.getConsultsByTrainerId(trainer.getId());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/member")
    @PreAuthorize("hasAnyRole('MEMBER')")
    @Operation(summary = "회원별 상담 정보 조회", description = "로그인한 회원의 상담 정보 목록을 조회합니다.")
    public ResponseEntity<List<ConsultResponseDTO>> getConsultsByMember(
            @AuthenticationPrincipal MemberDTO member) {
        log.info("Fetching consultations for member: {}", member.getEmail());
        List<ConsultResponseDTO> responseDTOs = consultService.getConsultsByMemberId(member.getId());
        return ResponseEntity.ok(responseDTOs);
    }
} 