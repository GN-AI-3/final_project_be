package com.example.final_project_be.domain.chatmessage.controller;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.service.ChatMessageService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "chat - api", description = "")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping("/{memberId}/recent")
    public ResponseEntity<List<ChatMessageResponseDTO>> getRecentMessages(
            @PathVariable Long memberId
    ) {
        List<ChatMessageResponseDTO> messages = chatMessageService.getRecentMessages(memberId, 20);
        return ResponseEntity.ok(messages);
    }

    
}
