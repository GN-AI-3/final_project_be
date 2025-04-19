package com.example.final_project_be.domain.consult.controller;

import com.example.final_project_be.domain.consult.dto.ConsultRequestDTO;
import com.example.final_project_be.domain.consult.dto.ConsultResponseDTO;
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
@Tag(name = "상담 관리", description = "상담 정보 API")
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

    @GetMapping("/member")
    @PreAuthorize("hasAnyRole('MEMBER')")
    @Operation(summary = "회원 자신의 상담 일지 조회", description = "로그인한 회원의 상담 일지 목록을 조회합니다.")
    public ResponseEntity<List<ConsultResponseDTO>> getMyConsults(
            @AuthenticationPrincipal MemberDTO member) {
        log.info("Member {} is fetching their own consultations", member.getEmail());
        List<ConsultResponseDTO> responseDTOs = consultService.getConsultsByMemberId(member.getId());
        return ResponseEntity.ok(responseDTOs);
    }
    
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('TRAINER')")
    @Operation(summary = "트레이너가 회원의 상담 일지 조회", description = "트레이너가 특정 회원의 상담 일지를 조회합니다.")
    public ResponseEntity<List<ConsultResponseDTO>> getMemberConsults(
            @PathVariable Long memberId,
            @AuthenticationPrincipal TrainerDTO trainer) {
        log.info("Trainer {} is fetching consultations for member ID: {}", trainer.getEmail(), memberId);
        List<ConsultResponseDTO> responseDTOs = consultService.getConsultsByMemberId(memberId);
        return ResponseEntity.ok(responseDTOs);
    }
} 