package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.ChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.example.final_project_be.domain.chatmessage.repository.ChatMessageRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.fasterxml.jackson.databind.ObjectMapper;

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
    @Transactional
    public ChatMessageResponseDTO saveMessage(String content, String email) {
        log.info("메시지 저장 요청 - 회원 이메일: {}, 내용: {}", email, content);

        // 회원 정보 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 이메일: " + email));

        try {
            // 1. 사용자 메시지 저장
            ChatMessage userMessage = ChatMessage.builder()
                    .content(content)
                    .role("user")
                    .member(member)
                    .build();

            userMessage = chatMessageRepository.save(userMessage);
            log.debug("사용자 메시지 저장 완료 - ID: {}", userMessage.getId());

            // 2. AI 서버 호출 및 응답 처리
            Map<String, Object> aiResponseData = sendToAiServerAndGetFullResponse(member, content);
            
            // 응답 텍스트 추출
            String aiResponseText = extractResponseText(aiResponseData);
            log.debug("AI 응답 텍스트: {}", aiResponseText);
            
            // 3. AI 응답 메시지 저장 (AI 서버 응답 데이터 포함)
            ChatMessage aiMessage = ChatMessage.builder()
                    .content(aiResponseText)
                    .role("assistant")
                    .member(member)
                    .build();
            
            // 확장 필드는 별도로 설정 (null 체크 추가)
            try {
                aiMessage.setServerMemberId(getStringValueSafely(aiResponseData, "member_id"));
                aiMessage.setTimestamp(getStringValueSafely(aiResponseData, "timestamp"));
                aiMessage.setMemberInput(getStringValueSafely(aiResponseData, "member_input"));
                aiMessage.setClarifiedInput(getStringValueSafely(aiResponseData, "clarified_input"));
                aiMessage.setSelectedAgents(convertToJsonString(aiResponseData.get("selected_agents")));
                aiMessage.setInjectedContext(convertToJsonString(aiResponseData.get("injected_context")));
                aiMessage.setAgentOutputs(convertToJsonString(aiResponseData.get("agent_outputs")));
                aiMessage.setFinalResponse(getStringValueSafely(aiResponseData, "final_response"));
                
                // executionTime 처리 (Number 타입 검사 추가)
                if (aiResponseData.containsKey("executionTime") && aiResponseData.get("executionTime") instanceof Number) {
                    aiMessage.setExecutionTime(((Number) aiResponseData.get("executionTime")).floatValue());
                } else if (aiResponseData.containsKey("execution_time") && aiResponseData.get("execution_time") instanceof Number) {
                    aiMessage.setExecutionTime(((Number) aiResponseData.get("execution_time")).floatValue());
                }
                
                log.debug("AI 메시지 확장 필드 설정 완료");
            } catch (Exception e) {
                // 확장 필드 설정 중 오류 발생 시 로그만 남기고 계속 진행
                log.warn("AI 메시지 확장 필드 설정 중 오류 발생: {}", e.getMessage());
            }

            ChatMessage savedAiMessage = chatMessageRepository.save(aiMessage);
            log.debug("AI 응답 메시지 저장 완료 - ID: {}", savedAiMessage.getId());

            return ChatMessageResponseDTO.from(savedAiMessage);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다.", e);
        }
    }

    // AI 서버에 요청하고 전체 응답을 Map으로 반환
    private Map<String, Object> sendToAiServerAndGetFullResponse(Member member, String content) {
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
                // 기본 응답 생성
                Map<String, Object> defaultResponse = new HashMap<>();
                defaultResponse.put("content", "죄송합니다. AI 응답을 받지 못했습니다.");
                return defaultResponse;
            }
            
            return response.getBody();
        } catch (RestClientException e) {
            log.error("AI 서버 통신 오류", e);
            throw new RuntimeException("AI 서버와 통신 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("AI 응답 처리 중 예상치 못한 오류", e);
            throw new RuntimeException("AI 응답 처리 중 오류가 발생했습니다.", e);
        }
    }
    
    // 응답 텍스트 추출 (final_response 또는 response 필드)
    private String extractResponseText(Map<String, Object> responseData) {
        if (responseData.containsKey("content")) {
            return (String) responseData.get("content");
        } else if (responseData.containsKey("final_response")) {
            return (String) responseData.get("final_response");
        } else if (responseData.containsKey("response")) {
            return (String) responseData.get("response");
        } else {
            // 응답 내용이 없는 경우
            log.warn("AI 서버 응답에 필요한 응답 필드가 없습니다.");
            return "죄송합니다. AI 응답 형식이 올바르지 않습니다.";
        }
    }
    
    // 객체를 JSON 문자열로 변환
    private String convertToJsonString(Object obj) {
        if (obj == null) {
            return null;
        }
        
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            log.warn("객체 JSON 변환 중 오류: {}", e.getMessage());
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatMessageResponseDTO> getRecentMessages(String email, int limit) {
        log.info("최근 메시지 조회 - 회원 이메일: {}, 제한: {}", email, limit);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. 이메일: " + email));

        // Pageable을 사용하여 최근 메시지 조회
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<ChatMessage> messages = chatMessageRepository.findByMemberOrderByCreatedAtDesc(member, pageable);

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
            // AI 서버 호출 및 응답 처리
            Map<String, Object> aiResponseData = sendToAiServerAndGetFullResponse(null, content);
            String aiResponseText = extractResponseText(aiResponseData);

            return ChatMessageResponseDTO.builder()
                    .content(aiResponseText)
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

    // 안전하게 Map에서 String 값 추출
    private String getStringValueSafely(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return null;
        }
        return map.get(key).toString();
    }
}