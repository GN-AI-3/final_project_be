package com.example.final_project_be.domain.chatmessage.controller;

import com.example.final_project_be.domain.chatmessage.dto.TrainerChatMessageRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.TrainerChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.dto.PtLogRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.PtLogResponseDTO;
import com.example.final_project_be.domain.chatmessage.service.TrainerChatMessageService;
import com.example.final_project_be.security.TrainerDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/trainer/chat")
@RequiredArgsConstructor
@Tag(name = "trainer-chat-api", description = "트레이너용 채팅 관련 API")
public class TrainerChatMessageController {

    private final TrainerChatMessageService trainerChatMessageService;

    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "트레이너 메시지 전송", description = "트레이너 메시지를 전송하고 AI 응답을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공", 
                    content = @Content(schema = @Schema(implementation = TrainerChatMessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal TrainerDTO trainer,
            @Valid @RequestBody TrainerChatMessageRequestDTO request,
            BindingResult bindingResult) {

        if (trainer == null) {
            log.warn("인증된 트레이너 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 서비스입니다."));
        }

        log.info("트레이너 메시지 전송 요청 - 트레이너 이메일: {}, 내용: {}", trainer.getEmail(), request.getContent());

        // 유효성 검사 실패 시 오류 응답
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();

            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            log.warn("유효성 검사 실패: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            // 로그인한 트레이너의 이메일을 사용
            TrainerChatMessageResponseDTO response = trainerChatMessageService.saveMessage(
                    request.getContent(),
                    trainer.getEmail()
            );
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("요청 처리 중 오류", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "메시지 처리 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "트레이너 최근 메시지 조회", description = "로그인한 트레이너의 최근 채팅 메시지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentMessages(@AuthenticationPrincipal TrainerDTO trainer) {
        if (trainer == null) {
            log.warn("인증된 트레이너 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 서비스입니다."));
        }
        
        log.info("최근 메시지 조회 요청 - 트레이너 이메일: {}", trainer.getEmail());
        
        try {
            List<TrainerChatMessageResponseDTO> messages = trainerChatMessageService.getRecentMessages(trainer.getEmail(), 20);
            return ResponseEntity.ok(messages);
        } catch (IllegalArgumentException e) {
            log.error("요청 처리 중 오류", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("서버 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "메시지 조회 중 오류가 발생했습니다."));
        }
    }

    @PreAuthorize("hasRole('TRAINER')")
    @Operation(summary = "PT 일지 챗봇", description = "PT 일지 챗봇에 메시지를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "PT 로그 전송 성공", 
                    content = @Content(schema = @Schema(implementation = PtLogResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/pt_log")
    public ResponseEntity<?> sendPtLog(
            @AuthenticationPrincipal TrainerDTO trainer,
            @Valid @RequestBody PtLogRequestDTO request,
            BindingResult bindingResult) {

        if (trainer == null) {
            log.warn("인증된 트레이너 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 서비스입니다."));
        }

        log.info("PT 로그 전송 요청 - 트레이너 이메일: {}, PT 스케줄 ID: {}, 메시지: {}", 
                trainer.getEmail(), request.getPtScheduleId(), request.getMessage());

        // 유효성 검사 실패 시 오류 응답
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            log.warn("유효성 검사 실패: {}", errors);
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            PtLogResponseDTO response = trainerChatMessageService.sendPtLogToFastAPI(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("PT 로그 전송 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "PT 로그 전송 중 오류가 발생했습니다."));
        }
    }
} 