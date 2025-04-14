package com.example.final_project_be.domain.chatmessage.dto;

import com.example.final_project_be.domain.chatmessage.entity.TrainerChatMessage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // null 값은 JSON 변환 시 제외
public class TrainerChatMessageResponseDTO {
    // 기본 필드 (클라이언트에 항상 반환되는 필드)
    private Long id;
    private String content;
    private String role;
    private LocalDateTime createdAt;
    
//    // 확장 필드 (내부 저장용, 필요시에만 반환)
//    @JsonIgnore
//    private String serverMemberId;
//
//    @JsonIgnore
//    private String timestamp;
//
//    @JsonIgnore
//    private String memberInput;
//
//    @JsonIgnore
//    private String clarifiedInput;
//
//    @JsonIgnore
//    private String selectedAgents;
//
//    @JsonIgnore
//    private String injectedContext;
//
//    @JsonIgnore
//    private String agentOutputs;
//
//    @JsonIgnore
//    private String finalResponse;
//
//    @JsonIgnore
//    private Float executionTime;

    /**
     * 기본 필드만 포함하는 DTO 생성
     */
    public static TrainerChatMessageResponseDTO basic(TrainerChatMessage message) {
        return TrainerChatMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
                .build();
    }

    /**
     * 모든 필드를 포함하는 DTO 생성
     */
    public static TrainerChatMessageResponseDTO from(TrainerChatMessage message) {
        return TrainerChatMessageResponseDTO.builder()
                .id(message.getId())
                .content(message.getContent())
                .role(message.getRole())
                .createdAt(message.getCreatedAt())
//                .serverMemberId(message.getServerMemberId())
//                .timestamp(message.getTimestamp())
//                .memberInput(message.getMemberInput())
//                .clarifiedInput(message.getClarifiedInput())
//                .selectedAgents(message.getSelectedAgents())
//                .injectedContext(message.getInjectedContext())
//                .agentOutputs(message.getAgentOutputs())
//                .finalResponse(message.getFinalResponse())
//                .executionTime(message.getExecutionTime())
                .build();
    }
} 