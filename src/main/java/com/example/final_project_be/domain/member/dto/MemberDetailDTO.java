package com.example.final_project_be.domain.member.dto;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.enums.MemberGoal;
import com.example.final_project_be.domain.member.enums.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Schema(description = "회원 정보 조회와 수정을 위한 dto")
public class MemberDetailDTO {

    private String email;
    private String phone;
    private String name;
    private String profileImage;
    private List<String> role;
    private List<String> goal;

    public static MemberDetailDTO from(Member member) {
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
