package com.example.final_project_be.domain.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "diet_plans")
@EntityListeners(AuditingEntityListener.class)
public class DietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "breakfast_suggestion")
    private String breakfastSuggestion;

    @Column(name = "lunch_suggestion")
    private String lunchSuggestion;

    @Column(name = "dinner_suggestion")
    private String dinnerSuggestion;

    private String breakfast;

    private String lunch;

    private String dinner;

    @Column(name = "diet_type")
    private String dietType;

    @Column(name = "user_gender")
    private String userGender;
}