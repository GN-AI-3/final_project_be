package com.example.final_project_be.domain.trainer.entity;

import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "trainer")
@ToString
public class Trainer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    
    private String password;
    private String name;
    private String profileImage;
    private String phone;
    
    @Column(nullable = false)
    @Builder.Default
    private String userType = "TRAINER";
    
    @Column
    private String fcmToken;
    
    @Column
    private String career;
    
    @Column
    private String certification;
    
    @Column
    private String introduction;
    
    @Column
    private String speciality;



    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    public void updateProfile(String name, String phone, String career, String certification, 
                             String introduction, String speciality) {
        this.name = name;
        this.phone = phone;
        this.career = career;
        this.certification = certification;
        this.introduction = introduction;
        this.speciality = speciality;
    }
    
    public void updatePassword(String password) {
        this.password = password;
    }
    
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public static Trainer from(TrainerJoinRequestDTO request) {
        return Trainer.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phone(request.getPhone())
                .profileImage("354dd23b-ee2e-4b35-91e0-9d8ef62219d6-default_image.png")
                .fcmToken(request.getFcmToken())
                .career(request.getCareer())
                .certification(request.getCertification())
                .introduction(request.getIntroduction())
                .speciality(request.getSpeciality())
                .build();
    }
} 