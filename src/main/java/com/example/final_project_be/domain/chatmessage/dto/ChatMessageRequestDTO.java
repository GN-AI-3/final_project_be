package com.example.final_project_be.domain.chatmessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChatMessageRequestDTO {

    private String content;
    private String role;
    private Long memberId;
}
