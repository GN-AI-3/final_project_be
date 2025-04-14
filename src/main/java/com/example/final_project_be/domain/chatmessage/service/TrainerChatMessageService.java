package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.TrainerChatMessageResponseDTO;
import java.util.List;

/**
 * 트레이너 채팅 메시지 서비스 인터페이스
 */
public interface TrainerChatMessageService {

    /**
     * 트레이너 메시지를 저장하고 AI 응답을 받아 저장합니다.
     *
     * @param content 트레이너 메시지 내용
     * @param email 트레이너 이메일
     * @return AI 응답 메시지
     */
    TrainerChatMessageResponseDTO saveMessage(String content, String email);

    /**
     * 특정 트레이너의 최근 메시지를 조회합니다.
     *
     * @param email 트레이너 이메일
     * @param limit 조회할 메시지 수
     * @return 메시지 목록
     */
    List<TrainerChatMessageResponseDTO> getRecentMessages(String email, int limit);
} 