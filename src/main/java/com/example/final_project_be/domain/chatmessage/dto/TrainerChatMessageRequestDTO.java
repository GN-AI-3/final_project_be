package com.example.final_project_be.domain.chatmessage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainerChatMessageRequestDTO {
    
    @NotBlank(message = "메시지 내용은 필수입니다.")
    @Size(min = 1, max = 1000, message = "메시지는 1~1000자 사이여야 합니다.")
    @Schema(description = "메시지 내용", example = "OOO 회원님을 위한 운동 루틴 만들어주라.")
    private String content;

    @Schema(description = "메시지 역할 (기본값: trainer)", example = "trainer", defaultValue = "trainer")
    private String role = "trainer";
} 