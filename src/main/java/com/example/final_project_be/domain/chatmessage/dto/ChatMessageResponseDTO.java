package com.example.final_project_be.domain.chatmessage.dto;

import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ChatMessageResponseDTO {
    private Long id;
    private String content;
    private String role;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDTO from(ChatMessage message) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
                .build();
    }

}
