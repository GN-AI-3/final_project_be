package com.example.final_project_be.domain.chatmessage.controller;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.service.ChatMessageService;
import com.example.final_project_be.security.MemberDTO;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "chat - api", description = "채팅 관련 API")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "메시지 전송", description = "사용자 메시지를 전송하고 AI 응답을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공", 
                    content = @Content(schema = @Schema(implementation = ChatMessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(
            @AuthenticationPrincipal MemberDTO member,
            @Valid @RequestBody ChatMessageRequestDTO request,
            BindingResult bindingResult) {

        if (member == null) {
            log.warn("인증된 회원 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 서비스입니다."));
        }

        log.info("메시지 전송 요청 - 회원 이메일: {}, 내용: {}", member.getEmail(), request.getContent());

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
            // 로그인한 사용자의 이메일을 사용
            ChatMessageResponseDTO response = chatMessageService.saveMessage(
                    request.getContent(),
                    member.getEmail()
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

    @Operation(summary = "최근 메시지 조회", description = "로그인한 회원의 최근 채팅 메시지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/recent")
    public ResponseEntity<?> getRecentMessages(@AuthenticationPrincipal MemberDTO member) {
        if (member == null) {
            log.warn("인증된 회원 정보가 없습니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "로그인이 필요한 서비스입니다."));
        }
        
        log.info("최근 메시지 조회 요청 - 회원 이메일: {}", member.getEmail());
        
        try {
            List<ChatMessageResponseDTO> messages = chatMessageService.getRecentMessages(member.getEmail(), 20);
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
}
