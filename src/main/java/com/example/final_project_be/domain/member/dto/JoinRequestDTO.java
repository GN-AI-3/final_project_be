package com.example.final_project_be.domain.member.dto;

import com.example.final_project_be.domain.member.enums.MemberGoal;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Schema(description = "회원가입 요청 DTO")
public class JoinRequestDTO {

    @NotBlank(message = "이메일 필수 입력 항목 입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    @NotBlank(message = "성별은 필수 입력 값입니다.")
    private String gender;

    @Schema(description = "사용자 유형", example = "MEMBER", defaultValue = "MEMBER")
    @Builder.Default
    private String userType = "MEMBER";

    @Schema(description = "목표 목록", example = "[\"WEIGHT_LOSS\", \"STRENGTH\", \"MENTAL_HEALTH\", \"HEALTH_MAINTENANCE\", \"BODY_SHAPE\", \"HOBBY\"]")
//    @NotEmpty(message = "목표는 필수 입력 값입니다.")
    private List<MemberGoal> goal;

    private String fcmToken;
}