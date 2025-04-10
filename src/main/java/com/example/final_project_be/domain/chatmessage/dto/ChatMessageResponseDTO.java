package com.example.final_project_be.domain.chatmessage.dto;

import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON 변환 시 제외
public class ChatMessageResponseDTO {
    private Long id;
    private String content;
    private String role;
    private LocalDateTime createdAt;
    
    // AI 서버 응답 관련 필드
    private String serverMemberId;
    private String timestamp;
    private String memberInput;
    private String clarifiedInput;
    private String selectedAgents;
    private String injectedContext;
    private String agentOutputs;
    private String finalResponse;
    private Float executionTime;

    public static ChatMessageResponseDTO from(ChatMessage message) {
        return ChatMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
                .serverMemberId(message.getServerMemberId())
                .timestamp(message.getTimestamp())
                .memberInput(message.getMemberInput())
                .clarifiedInput(message.getClarifiedInput())
                .selectedAgents(message.getSelectedAgents())
                .injectedContext(message.getInjectedContext())
                .agentOutputs(message.getAgentOutputs())
                .finalResponse(message.getFinalResponse())
                .executionTime(message.getExecutionTime())
                .build();
    }
}
