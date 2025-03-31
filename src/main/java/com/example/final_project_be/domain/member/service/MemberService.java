package com.example.final_project_be.domain.member.service;

import com.example.final_project_be.domain.member.dto.JoinRequestDTO;
import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.enums.MemberGoal;
import com.example.final_project_be.domain.member.enums.MemberRole;
import com.example.final_project_be.security.MemberDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;
import java.util.stream.Collectors;

public interface MemberService {
    void join(@Valid JoinRequestDTO joinRequestDTO);

    Map<String, Object> login(@NotBlank(message = "이메일을 입력해주세요") String email, @NotBlank(message = "패스워드를  입력해주세요") String password);

    Member getEntity(String email);

    MemberDetailDTO getMyInfo(String email);

    Boolean checkEmail(String email);


    default MemberDTO entityToDTO(Member member) {

        return new MemberDTO(
                member.getEmail(),
                member.getPassword(),
                member.getName(),
                member.getPhone(),
                member.getMemberRoleList().stream()
                        .map(Enum::name).toList(),
                member.getMemberGoalList().stream()
                        .map(Enum::name).toList()
        );
    }

    default MemberDetailDTO entityToMemberDetailDTO(Member member) {
        return MemberDetailDTO.builder()
                .email(member.getEmail())
                .phone(member.getPhone())
                .name(member.getName())
                .profileImage(member.getProfileImage())
                .role(member.getMemberRoleList().stream()
                        .map(MemberRole::getRoleName)
                        .collect(Collectors.toList()))
                .goal(member.getMemberGoalList().stream()
                        .map(MemberGoal::getGoal)
                        .collect(Collectors.toList()))
                .build();
    }


}
