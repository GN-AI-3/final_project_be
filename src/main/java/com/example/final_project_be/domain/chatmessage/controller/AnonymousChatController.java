package com.example.final_project_be.domain.chatmessage.controller;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.service.ChatMessageService;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/anonymous-chat")
@RequiredArgsConstructor
@Tag(name = "anonymous-chat - api", description = "익명 채팅 관련 API")
public class AnonymousChatController {

    private final ChatMessageService chatMessageService;

    @Operation(summary = "익명 메시지 전송", description = "로그인 없이 메시지를 전송하고 AI 응답을 받습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공", 
                    content = @Content(schema = @Schema(implementation = ChatMessageResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/send")
    public ResponseEntity<?> sendAnonymousMessage(
            @Valid @RequestBody ChatMessageRequestDTO request,
            BindingResult bindingResult) {
        
        log.info("익명 메시지 전송 요청 - 내용: {}", request.getContent());
        
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
            // 익명 사용자를 위한 응답 생성 (저장 없이 즉시 응답)
            ChatMessageResponseDTO response = chatMessageService.generateAnonymousResponse(request.getContent());
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
} 