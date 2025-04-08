package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;

import java.util.List;

public interface ChatMessageService {

    /**
     * 사용자 메시지를 저장하고 AI 응답을 생성하여 저장 후 반환합니다.
     * 
     * @param content 사용자 메시지 내용
     * @param email 사용자 이메일
     * @return AI 응답 메시지 DTO
     */
    ChatMessageResponseDTO saveMessage(String content, String email);

    /**
     * 특정 회원의 최근 메시지를 조회합니다.
     * 
     * @param email 사용자 이메일
     * @param limit 조회할 메시지 수 제한
     * @return 메시지 목록
     */
    List<ChatMessageResponseDTO> getRecentMessages(String email, int limit);
}
