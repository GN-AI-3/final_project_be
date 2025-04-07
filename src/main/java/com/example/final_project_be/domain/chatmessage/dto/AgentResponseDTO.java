package com.example.final_project_be.domain.chatmessage.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
public class AgentResponseDTO {

    private String agent;
    private String type;
    private Map<String, Object> result;
}
