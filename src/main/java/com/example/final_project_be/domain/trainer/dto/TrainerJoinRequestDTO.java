package com.example.final_project_be.domain.trainer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Schema(description = "트레이너 회원가입 요청 DTO")
public class TrainerJoinRequestDTO {

    @NotBlank(message = "이메일 필수 입력 항목 입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    private String name;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    @Schema(description = "사용자 유형", example = "TRAINER", defaultValue = "TRAINER")
    @Builder.Default
    private String userType = "TRAINER";

    @Schema(description = "경력", example = "헬스트레이너 10년")
    private String career;
    
    @Schema(description = "자격증 목록", example = "[\"생활스포츠지도사 2급\", \"건강운동관리사\"]")
    @Builder.Default
    private List<String> certifications = new ArrayList<>();
    
    @Schema(description = "소개", example = "안녕하세요, 10년차 PT 트레이너입니다.")
    private String introduction;
    
    @Schema(description = "전문분야 목록", example = "[\"체중감량\", \"근력강화\", \"자세교정\"]")
    @Builder.Default
    private List<String> specialities = new ArrayList<>();

    private String fcmToken;
} 