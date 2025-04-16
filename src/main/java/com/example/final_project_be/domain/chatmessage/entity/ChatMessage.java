package com.example.final_project_be.domain.chatmessage.entity;

import com.example.final_project_be.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chat_message")
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // user or assistant
    @Column(nullable = false, length = 20)
    private String role;

    // Member와 직접 연관관계 맺기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // AI 서버 응답 관련 필드
    @Column(name = "server_member_id")
    private String serverMemberId;

    // AI 서버에서 받은 타임스탬프 문자열
    @Column(name = "timestamp")
    private String timestamp;

    // 생성 시간 자동 설정
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "member_input", columnDefinition = "TEXT")
    private String memberInput;

    @Column(name = "clarified_input", columnDefinition = "TEXT")
    private String clarifiedInput;

    @Column(name = "selected_agents", columnDefinition = "TEXT")
    private String selectedAgents;
    
    @Column(name = "injected_context", columnDefinition = "TEXT")
    private String injectedContext;

    @Column(name = "agent_outputs", columnDefinition = "TEXT")
    private String agentOutputs;

    @Column(name = "final_response", columnDefinition = "TEXT")
    private String finalResponse;

    @Column(name = "execution_time")
    private Float executionTime;
}
