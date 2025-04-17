package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import java.util.List;

/**
 * 채팅 메시지 서비스 인터페이스
 */
public interface ChatMessageService {

    /**
     * 메시지를 저장하고 AI 응답을 받아 저장합니다.
     *
     * @param content 사용자 메시지 내용
     * @param email 사용자 이메일
     * @return AI 응답 메시지
     */
    ChatMessageResponseDTO saveMessage(String content, String email);

    /**
     * 특정 사용자의 최근 메시지를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param limit 조회할 메시지 수
     * @return 메시지 목록
     */
    List<ChatMessageResponseDTO> getRecentMessages(String email, int limit);

    /**
     * 익명 사용자를 위한 AI 응답을 생성합니다.
     * 메시지는 저장되지 않고 즉시 응답만 반환합니다.
     *
     * @param content 사용자 메시지 내용
     * @return AI 응답 메시지
     */
    ChatMessageResponseDTO generateAnonymousResponse(String content);
}