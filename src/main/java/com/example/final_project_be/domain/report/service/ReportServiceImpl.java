package com.example.final_project_be.domain.report.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final RestTemplate restTemplate;

    @Value("${app.ai-server.url}")
    private String aiServerBaseUrl;

    @Override
    public Map<String, Object> callFastApiReport(Long ptContractId) {
        log.info("FastAPI 보고서 생성 요청 시작 - PT 계약 ID: {}", ptContractId);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String fastApiUrl = UriComponentsBuilder.fromHttpUrl(aiServerBaseUrl)
                    .path("/report")
                    .queryParam("ptContractId", ptContractId)
                    .build()
                    .toUriString();

            Map<String, Object> response = restTemplate.exchange(
                fastApiUrl,
                HttpMethod.POST,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {}
            ).getBody();

            if (response == null) {
                throw new RuntimeException("FastAPI 서버 응답이 비어있습니다.");
            }

            log.info("FastAPI 보고서 생성 완료");
            return response;
        } catch (Exception e) {
            log.error("FastAPI 서버 통신 오류", e);
            throw new RuntimeException("FastAPI 서버와 통신 중 오류가 발생했습니다.", e);
        }
    }
} 