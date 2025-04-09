package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.example.final_project_be.domain.chatmessage.repository.ChatMessageRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
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

    @Value("${app.ai-server.url}")
    private String aiServerBaseUrl;

    @Override
    public ChatMessageResponseDTO saveMessage(String content, String email) {
        log.info("메시지 저장 요청 - 회원 이메일: {}, 내용: {}", email, content);

        // 회원 정보 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 이메일: " + email));

        try {
            // 1. 사용자 메시지 저장
            ChatMessage userMessage = ChatMessage.builder()
                    .content(content)
                    .role("member")
                    .member(member)
                    .build();

            chatMessageRepository.save(userMessage);
            log.debug("사용자 메시지 저장 완료 - ID: {}", userMessage.getId());

            // 2. AI 서버 호출 - 회원 객체 전달 (수정된 부분)
            String aiResponse;
            try {
                aiResponse = sendToAiServer(member, content);
                log.debug("AI 서버 응답 받음 - 길이: {}", aiResponse.length());
            } catch (Exception e) {
                // AI 서버 호출 실패 시 기본 응답 제공
                log.error("AI 서버 호출 실패", e);
                aiResponse = "죄송합니다. 현재 AI 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.";
            }

            // 3. AI 응답 메시지 저장
            ChatMessage aiMessage = ChatMessage.builder()
                    .content(aiResponse)
                    .role("assistant")
                    .member(member)
                    .build();

            ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);
            log.debug("AI 응답 메시지 저장 완료 - ID: {}", savedAiMessage.getId());

            return ChatMessageResponseDTO.from(savedAiMessage);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다.", e);
        }
    }

    // 메소드 시그니처 변경: Member 객체를 받도록 수정 (수정된 부분)
    private String sendToAiServer(Member member, String content) {
        log.info("AI 서버 호출 - 회원: {}, 메시지 길이: {}", 
                member != null ? member.getEmail() : "익명", 
                content.length());

        Map<String, Object> requestBody = new HashMap<>();
        if (member != null) {
            requestBody.put("member_id", member.getId().toString());
            requestBody.put("email", member.getEmail());
        }
        requestBody.put("message", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String aiApiUrl = aiServerBaseUrl + "/chat";

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(aiApiUrl, entity, Map.class);

            if (response.getBody() == null) {
                log.warn("AI 서버 응답이 비어있습니다.");
                return "죄송합니다. AI 응답을 받지 못했습니다.";
            }

            String aiResponse = (String) response.getBody().get("response");
            if (aiResponse == null || aiResponse.isEmpty()) {
                log.warn("AI 서버 응답에 'response' 필드가 없거나 비어있습니다.");
                return "죄송합니다. AI 응답 형식이 올바르지 않습니다.";
            }

            return aiResponse;
        } catch (RestClientException e) {
            log.error("AI 서버 통신 오류", e);
            throw new RuntimeException("AI 서버와 통신 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("AI 응답 처리 중 예상치 못한 오류", e);
            throw new RuntimeException("AI 응답 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatMessageResponseDTO> getRecentMessages(String email, int limit) {
        log.info("최근 메시지 조회 - 회원 이메일: {}, 제한: {}", email, limit);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 이메일: " + email));

        List<ChatMessage> messages = chatMessageRepository
                .findTop20ByMemberOrderByCreatedAtDesc(member);

        // 시간순으로 정렬 (최신순 -> 오래된순)
        Collections.reverse(messages);

        return messages.stream()
                .map(ChatMessageResponseDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public ChatMessageResponseDTO generateAnonymousResponse(String content) {
        log.info("익명 사용자 메시지 처리 시작 - 내용: {}", content);
        
        try {
            // AI 서버 호출
            String aiResponse = sendToAiServer(null, content);
            log.info("AI 서버 응답 받음 - 길이: {}", aiResponse.length());

            return ChatMessageResponseDTO.builder()
                    .content(aiResponse)
                    .role("assistant")
                    .createdAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("AI 서버 호출 실패", e);
            // AI 서버 호출 실패 시 기본 응답 제공
            return ChatMessageResponseDTO.builder()
                    .content("죄송합니다. 현재 AI 서비스에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.")
                    .role("assistant")
                    .createdAt(LocalDateTime.now())
                    .build();
        }
    }

    private String generateAIResponse(String content) {
        // TODO: 실제 AI 서버와 통신하는 로직 구현
        // 임시로 간단한 응답 생성
        return "AI 응답: " + content;
    }
}