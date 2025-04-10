package com.example.final_project_be.domain.chatmessage.repository;

import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.example.final_project_be.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findTop20ByMemberOrderByCreatedAtDesc(Member member);
    
    // 특정 회원의 특정 역할의 가장 최근 메시지 조회
    Optional<ChatMessage> findFirstByMemberAndRoleOrderByCreatedAtDesc(Member member, String role);
    
    // 파라미터에 따라 최근 메시지 조회 (Pageable 사용)
    List<ChatMessage> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
}
