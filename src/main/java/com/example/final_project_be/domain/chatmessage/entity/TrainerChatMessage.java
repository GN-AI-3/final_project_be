package com.example.final_project_be.domain.chatmessage.entity;

import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "trainer_chat_message")
public class TrainerChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // user or assistant
    @Column(nullable = false, length = 20)
    private String role;

    // Trainer와 직접 연관관계 맺기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trainer_id")
    private Trainer trainer;

    // AI 서버 응답 관련 필드
    @Column(name = "server_member_id")
    private String serverMemberId;

    @Column(name = "timestamp")
    private String timestamp;

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