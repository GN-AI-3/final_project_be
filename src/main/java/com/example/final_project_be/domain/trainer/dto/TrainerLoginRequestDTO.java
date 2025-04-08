package com.example.final_project_be.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "로그인 요청 DTO")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrainerLoginRequestDTO {

    @Schema(description = "이메일 주소", example = "trainer@example.com")
    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    private String email;

    @Schema(description = "비밀번호", example = "password123")
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;
} 