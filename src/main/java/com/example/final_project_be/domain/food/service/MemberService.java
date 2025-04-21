package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.MemberUpdateRequest;
import com.example.final_project_be.domain.food.repository.MemberGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberGoalRepository memberGoalRepository;

    @Transactional
    public void updateMember(MemberUpdateRequest request) {
        if (request.getGoal() != null && !request.getGoal().isEmpty()) {
            memberGoalRepository.updateMemberGoal(request.getMemberId(), request.getGoal());
        }
    }
}