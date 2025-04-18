package com.example.final_project_be.domain.trainer.entity;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@DynamicUpdate
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "trainer")
@ToString(exclude = { "ptContractList"})
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
    
    @ElementCollection
    @CollectionTable(name = "trainer_certification", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "certification")
    @Builder.Default
    private List<String> certifications = new ArrayList<>();
    
    @Column
    private String introduction;
    
    @ElementCollection
    @CollectionTable(name = "trainer_speciality", joinColumns = @JoinColumn(name = "trainer_id"))
    @Column(name = "speciality")
    @Builder.Default
    private List<String> specialities = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PtContract> ptContractList = new ArrayList<>();

    @OneToOne(mappedBy = "trainer", cascade = CascadeType.ALL)
    private Subscribe subscribe;

    @OneToOne(mappedBy = "trainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private TrainerWorkingTime trainerWorkingTime;

    @Column(name = "schedule_cancel_limit_hours", nullable = false)
    @Builder.Default
    private Integer scheduleCancelLimitHours = 12;

    @Column(name = "schedule_change_limit_hours", nullable = false)
    @Builder.Default
    private Integer scheduleChangeLimitHours = 12;

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    public void updateProfile(String name, String phone, String career, List<String> certifications, 
                             String introduction, List<String> specialities) {
        this.name = name;
        this.phone = phone;
        this.career = career;
        this.certifications = certifications;
        this.introduction = introduction;
        this.specialities = specialities;
    }
    
    public void updatePassword(String password) {
        this.password = password;
    }
    
    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public static Trainer from(TrainerJoinRequestDTO request) {
        // certification과 specialities가 null이면 빈 리스트로 초기화
        List<String> certifications = request.getCertifications();
        if (certifications == null) {
            certifications = new ArrayList<>();
        }
        
        List<String> specialities = request.getSpecialities();
        if (specialities == null) {
            specialities = new ArrayList<>();
        }
        
        return Trainer.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phone(request.getPhone())
                .profileImage("354dd23b-ee2e-4b35-91e0-9d8ef62219d6-default_image.png")
                .fcmToken(request.getFcmToken())
                .career(request.getCareer())
                .certifications(certifications)
                .introduction(request.getIntroduction())
                .specialities(specialities)
                .build();
    }
} 