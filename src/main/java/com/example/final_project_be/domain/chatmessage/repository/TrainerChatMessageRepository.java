package com.example.final_project_be.domain.chatmessage.repository;

import com.example.final_project_be.domain.chatmessage.entity.TrainerChatMessage;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerChatMessageRepository extends JpaRepository<TrainerChatMessage, Long> {

    List<TrainerChatMessage> findTop20ByTrainerOrderByCreatedAtDesc(Trainer trainer);
    
    // 특정 트레이너의 특정 역할의 가장 최근 메시지 조회
    Optional<TrainerChatMessage> findFirstByTrainerAndRoleOrderByCreatedAtDesc(Trainer trainer, String role);
    
    // 파라미터에 따라 최근 메시지 조회 (Pageable 사용)
    List<TrainerChatMessage> findByTrainerOrderByCreatedAtDesc(Trainer trainer, Pageable pageable);
} 