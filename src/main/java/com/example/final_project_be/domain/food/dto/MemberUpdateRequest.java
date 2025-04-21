package com.example.final_project_be.domain.food.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberUpdateRequest {
    private Long memberId;
    private String goal;
}