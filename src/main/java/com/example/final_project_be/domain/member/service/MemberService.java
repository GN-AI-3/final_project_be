package com.example.final_project_be.domain.member.service;

import com.example.final_project_be.domain.member.dto.JoinRequestDTO;
import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.security.MemberDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public interface MemberService {
    void join(@Valid JoinRequestDTO joinRequestDTO);

    Map<String, Object> login(@NotBlank(message = "이메일을 입력해주세요") String email, @NotBlank(message = "패스워드를  입력해주세요") String password);

    Member getEntity(String email);

    MemberDetailDTO getMyInfo(String email);

    Boolean checkEmail(String email);

    MemberDetailDTO getMemberInfo(Long memberId);

    default MemberDTO entityToDTO(Member member) {
        return new MemberDTO(
                member.getId(),
                member.getEmail(),
                member.getPassword(),
                member.getPhone(),
                member.getName(),
                member.getUserType(),
                member.getMemberGoalList().stream()
                        .map(Enum::name).toList()
        );
    }

    default MemberDetailDTO entityToMemberDetailDTO(Member member) {
        return MemberDetailDTO.from(member);
    }


}
