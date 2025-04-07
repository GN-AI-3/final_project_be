package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.example.final_project_be.domain.chatmessage.repository.ChatMessageRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final MemberRepository memberRepository;
    private final RestTemplate restTemplate;


    @Override
    public ChatMessageResponseDTO saveMessage(ChatMessageRequestDTO request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

        // 1. 사용자 메시지 저장
        ChatMessage userMessage = chatMessageRepository.save(ChatMessage.builder()
                .content(request.getContent())
                .role("user")
                .member(member)
                .build());

        // 2. FastAPI 호출
        String aiResponse = sendToFastApi(request.getMemberId(), request.getContent());

        // 3. AI 응답 메시지 저장
        ChatMessage aiMessage = chatMessageRepository.save(ChatMessage.builder()
                .content(aiResponse)
                .role("assistant")
                .member(member)
                .build());

        return ChatMessageResponseDTO.from(aiMessage);
    }

    private String sendToFastApi(Long memberId, String content) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", memberId.toString());
        body.put("message", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // FastAPI 주소
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        String fastApiUrl = "http://localhost:8000/chat";

        ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, entity, Map.class);
        return (String) response.getBody().get("response");
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatMessageResponseDTO> getRecentMessages(Long memberId, int limit) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 멤버입니다."));

        List<ChatMessage> messages = chatMessageRepository
                .findTop20ByMemberOrderByCreatedAtDesc(member);

        Collections.reverse(messages);

        return messages.stream()
                .map(ChatMessageResponseDTO::from)
                .collect(Collectors.toList());
    }

}
