package com.example.final_project_be.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data

@Schema(description = "로그인 성공 시 응답하는 회원 정보가 담긴 dto")
public class LoginResponseDTO {

    private String email;
    private String name;
    private List<String> role;
    private String accessToken;
}
