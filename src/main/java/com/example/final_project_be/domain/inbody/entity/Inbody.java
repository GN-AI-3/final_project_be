package com.example.final_project_be.domain.inbody.entity;

import com.example.final_project_be.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@DynamicUpdate
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "inbody")
public class Inbody {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Inbody_id;

    private Integer weight;
    private Integer tall;
    private Integer bmi;
    private String date;

    private Double muscleMass;           // 골격근량 (kg)
    private Double fatMass;              // 체지방량 (kg)
    private Double bodyFatPercentage;    // 체지방률 (%)
    private Double bmr;                  // 기초대사량 (kcal)

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}