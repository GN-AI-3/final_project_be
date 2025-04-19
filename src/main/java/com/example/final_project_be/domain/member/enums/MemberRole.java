package com.example.final_project_be.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberRole {
    USER("유저"), TRAINER("트레이너");

    private final String roleName;
}
