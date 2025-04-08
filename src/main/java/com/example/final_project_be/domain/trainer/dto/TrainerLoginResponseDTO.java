package com.example.final_project_be.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "트레이너 로그인 성공 시 응답하는 정보가 담긴 dto")
public class TrainerLoginResponseDTO {

    private String email;
    private String name;
    private String userType;
    private String accessToken;
    private String career;
    private String speciality;
} 