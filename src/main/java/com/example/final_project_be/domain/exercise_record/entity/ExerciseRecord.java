package com.example.final_project_be.domain.exercise_record.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.entity.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
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
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exercise_record")
public class ExerciseRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private LocalDateTime date;

    @Column(columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private JsonNode recordData;

    @Column(columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private JsonNode memoData;

}
