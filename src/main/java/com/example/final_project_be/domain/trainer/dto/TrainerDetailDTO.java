package com.example.final_project_be.domain.trainer.dto;

import com.example.final_project_be.domain.trainer.entity.Trainer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "트레이너 상세 정보 DTO")
public class TrainerDetailDTO {

    private Long id;
    private String email;
    private String name;
    private String profileImage;
    private String phone;
    private String userType;
    private String career;
    private String certification;
    private String introduction;
    private String speciality;
    
    public static TrainerDetailDTO from(Trainer trainer) {
        return TrainerDetailDTO.builder()
                .id(trainer.getId())
                .email(trainer.getEmail())
                .name(trainer.getName())
                .profileImage(trainer.getProfileImage())
                .phone(trainer.getPhone())
                .userType(trainer.getUserType())
                .career(trainer.getCareer())
                .certification(trainer.getCertification())
                .introduction(trainer.getIntroduction())
                .speciality(trainer.getSpeciality())
                .build();
    }
} 