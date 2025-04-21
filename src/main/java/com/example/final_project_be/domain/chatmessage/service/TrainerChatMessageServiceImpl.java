package com.example.final_project_be.domain.chatmessage.service;

import com.example.final_project_be.domain.chatmessage.dto.PtLogRequestDTO;
import com.example.final_project_be.domain.chatmessage.dto.PtLogResponseDTO;
import com.example.final_project_be.domain.chatmessage.dto.TrainerChatMessageResponseDTO;
import com.example.final_project_be.domain.chatmessage.entity.TrainerChatMessage;
import com.example.final_project_be.domain.chatmessage.repository.TrainerChatMessageRepository;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainerChatMessageServiceImpl implements TrainerChatMessageService {

    private final TrainerChatMessageRepository trainerChatMessageRepository;
    private final TrainerRepository trainerRepository;
    private final RestTemplate restTemplate;

    @Value("${app.ai-server.url}")
    private String aiServerBaseUrl;

    @Override
    @Transactional
    public TrainerChatMessageResponseDTO saveMessage(String content, String email) {
        log.info("트레이너 메시지 저장 요청 - 트레이너 이메일: {}, 내용: {}", email, content);

        // 트레이너 정보 확인
        Trainer trainer = trainerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다. 이메일: " + email));

        try {
            // 1. 트레이너 메시지 저장
            TrainerChatMessage trainerMessage = TrainerChatMessage.builder()
                    .content(content)
                    .role("user")
                    .trainer(trainer)
                    .build();

            trainerMessage = trainerChatMessageRepository.save(trainerMessage);
            log.debug("트레이너 메시지 저장 완료 - ID: {}", trainerMessage.getId());

            // 2. AI 서버 호출 및 응답 처리
            Map<String, Object> aiResponseData = sendToAiServerAndGetFullResponse(trainer, content);
            
            // 응답 텍스트 추출
            String aiResponseText = extractResponseText(aiResponseData);
            log.debug("AI 응답 텍스트: {}", aiResponseText);
            
            // 3. AI 응답 메시지 저장 (AI 서버 응답 데이터 포함)
            TrainerChatMessage aiMessage = TrainerChatMessage.builder()
                    .content(aiResponseText)
                    .role("assistant")
                    .trainer(trainer)
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

            TrainerChatMessage savedAiMessage = trainerChatMessageRepository.save(aiMessage);
            log.debug("AI 응답 메시지 저장 완료 - ID: {}", savedAiMessage.getId());

            return TrainerChatMessageResponseDTO.from(savedAiMessage);
        } catch (Exception e) {
            log.error("메시지 처리 중 오류 발생", e);
            throw new RuntimeException("메시지 처리 중 오류가 발생했습니다.", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public List<TrainerChatMessageResponseDTO> getRecentMessages(String email, int limit) {
        log.info("최근 메시지 조회 - 트레이너 이메일: {}, 제한: {}", email, limit);

        Trainer trainer = trainerRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 트레이너입니다. 이메일: " + email));

        // Pageable을 사용하여 최근 메시지 조회
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<TrainerChatMessage> messages = trainerChatMessageRepository.findByTrainerOrderByCreatedAtDesc(trainer, pageable);

        // 시간순으로 정렬 (최신순 -> 오래된순)
        Collections.reverse(messages);

        return messages.stream()
                .map(TrainerChatMessageResponseDTO::from)
                .collect(Collectors.toList());
    }

    // AI 서버에 요청하고 전체 응답을 Map으로 반환
    private Map<String, Object> sendToAiServerAndGetFullResponse(Trainer trainer, String content) {
        log.info("AI 서버 호출 - 트레이너: {}, 메시지 길이: {}",
                trainer != null ? trainer.getEmail() : "익명",
                content.length());

        Map<String, Object> requestBody = new HashMap<>();
        if (trainer != null) {
            requestBody.put("trainer_id", trainer.getId().toString());
            requestBody.put("email", trainer.getEmail());
            requestBody.put("user_type", "TRAINER"); // 트레이너 타입 추가
        }
        requestBody.put("message", content);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        String aiApiUrl = aiServerBaseUrl + "/chat";  // 트레이너 전용 엔드포인트 사용 (필요시 변경)

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

    // 안전하게 Map에서 String 값 추출
    private String getStringValueSafely(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key) || map.get(key) == null) {
            return null;
        }
        return map.get(key).toString();
    }

    @Override
    public PtLogResponseDTO sendPtLogToFastAPI(PtLogRequestDTO request) {
        log.info("PT 로그 FastAPI 서버로 전송 시작 - PT 스케줄 ID: {}", request.getPtScheduleId());
        
        try {
            // FastAPI 요청 형식에 맞게 데이터 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", request.getMessage());
            requestBody.put("ptScheduleId", request.getPtScheduleId());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String fastApiUrl = aiServerBaseUrl + "/pt_log";
            
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl, entity, Map.class);
            
            if (response.getBody() == null) {
                throw new RuntimeException("FastAPI 서버 응답이 비어있습니다.");
            }

            // FastAPI 응답을 DTO로 변환
            Map<String, Object> responseBody = response.getBody();
            PtLogResponseDTO responseDTO = new PtLogResponseDTO();
            responseDTO.setPtScheduleId(request.getPtScheduleId());
            responseDTO.setTimestamp((String) responseBody.get("timestamp"));
            responseDTO.setFinalResponse((String) responseBody.get("final_response"));
            
            // execution_time이 Number 타입인지 확인
            Object executionTime = responseBody.get("execution_time");
            if (executionTime instanceof Number) {
                responseDTO.setExecutionTime(((Number) executionTime).doubleValue());
            } else {
                responseDTO.setExecutionTime(0.0);
            }

            log.info("PT 로그 FastAPI 서버 전송 완료");
            return responseDTO;
            
        } catch (RestClientException e) {
            log.error("FastAPI 서버 통신 오류", e);
            throw new RuntimeException("FastAPI 서버와 통신 중 오류가 발생했습니다.", e);
        } catch (Exception e) {
            log.error("PT 로그 전송 중 예상치 못한 오류", e);
            throw new RuntimeException("PT 로그 전송 중 오류가 발생했습니다.", e);
        }
    }
} 