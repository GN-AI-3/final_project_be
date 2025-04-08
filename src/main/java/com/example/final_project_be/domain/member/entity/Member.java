package com.example.final_project_be.domain.member.entity;


import com.example.final_project_be.domain.chatmessage.entity.ChatMessage;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.member.dto.JoinRequestDTO;
import com.example.final_project_be.domain.member.enums.MemberGoal;
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
@Table(name = "member")
@ToString(exclude = {"memberGoalList", "exerciseRecords", "chatMessages"})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String profileImage;
    private String goal;
    private String phone;

    @Column
    private String fcmToken;
    
    @Column(nullable = false)
    @Builder.Default
    private String userType = "MEMBER";

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "member_goal_list", joinColumns = @JoinColumn(name = "email"))
    @Enumerated(EnumType.STRING)
    @Column(name = "goal")
    @Builder.Default
    private List<MemberGoal> memberGoalList = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ExerciseRecord> exerciseRecords = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChatMessage> chatMessages = new ArrayList<>();

    public void updateFcmToken(String fcmToken) {this.fcmToken = fcmToken;}

    public static Member from(JoinRequestDTO request) {
        return Member.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .name(request.getName())
                .phone(request.getPhone())
                .profileImage("354dd23b-ee2e-4b35-91e0-9d8ef62219d6-default_image.png")
                .fcmToken(request.getFcmToken())
                .userType(request.getUserType())
                .memberGoalList(request.getGoal())
                .build();
    }
}
