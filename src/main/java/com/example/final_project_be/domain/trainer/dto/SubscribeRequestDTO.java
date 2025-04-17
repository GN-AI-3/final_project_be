package com.example.final_project_be.domain.trainer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequestDTO {
    
    @NotBlank(message = "구독 유형을 선택해주세요")
    private String subscriptionType; // BASIC, STANDARD, PREMIUM
}
