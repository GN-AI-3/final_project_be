package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponseDTO saveMessage(ChatMessageRequestDTO request);

    List<ChatMessageResponseDTO> getRecentMessages(Long memberId, int limit);
}
