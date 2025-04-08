package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.entity.Subscribe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeDTO {
    private Long id;
    private String name;
    private String price;
    private String management_person;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Subscribe.SubscriptionStatus status;
    
    public static SubscribeDTO from(Subscribe subscribe) {
        if (subscribe == null) {
            return null;
        }
        
        return SubscribeDTO.builder()
                .id(subscribe.getId())
                .name(subscribe.getName())
                .price(subscribe.getPrice())
                .management_person(subscribe.getManagement_person())
                .startDate(subscribe.getStartDate())
                .endDate(subscribe.getEndDate())
                .createdAt(subscribe.getCreatedAt())
                .modifiedAt(subscribe.getModifiedAt())
                .status(subscribe.getStatus())
                .build();
    }
} 