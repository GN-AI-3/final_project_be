package com.example.final_project_be.domain.inbody.entity;

import org.hibernate.annotations.DynamicUpdate;

import com.example.final_project_be.domain.member.entity.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}