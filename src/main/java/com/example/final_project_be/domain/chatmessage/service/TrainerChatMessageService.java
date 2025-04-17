package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.TrainerChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.dto.PtLogRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.PtLogResponseDTO;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Map;

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

    /**
     * PT 로그를 FastAPI 서버로 전송합니다.
     *
     * @param request PT 로그 요청 데이터
     * @return PT 로그 응답 데이터
     */
    PtLogResponseDTO sendPtLogToFastAPI(PtLogRequestDTO request);
} 