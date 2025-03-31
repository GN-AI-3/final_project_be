package com.example.final_project_be.domain.member.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MemberGoal {

    WEIGHT_LOSS("체중 감량"),
    STRENGTH("신체 능력 강화"),
    MENTAL_HEALTH("정신적 건강 관리"),
    HEALTH_MAINTENANCE("건강 유지"),
    BODY_SHAPE("체형 관리"),
    HOBBY("취미");

    private final String goal;
}
