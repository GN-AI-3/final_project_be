package com.example.final_project_be.domain.chatmessage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "채팅 메시지 요청 DTO")
public class ChatMessageRequestDTO {

    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "메시지는 1~1000자 사이여야 합니다.")
    @Schema(description = "메시지 내용", example = "오늘 어떤 운동을 해야 할까요?")
    private String content;
    
    @Schema(description = "메시지 역할 (기본값: member)", example = "member", defaultValue = "member")
    private String role = "member";
}
