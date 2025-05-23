package com.example.final_project_be.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class LoginRequestDTO {
    @Schema(description = "이메일", example = "user1@test.com")
    @NotBlank(message = "이메일을 입력해주세요")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    @NotBlank(message = "패스워드를 입력해주세요")
    private String password;
    
    @Schema(description = "FCM 토큰 (선택사항)")
    private String fcmToken;
}
