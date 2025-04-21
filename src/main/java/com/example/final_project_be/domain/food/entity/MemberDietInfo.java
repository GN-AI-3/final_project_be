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
@Table(name = "member_diet_info")
@EntityListeners(AuditingEntityListener.class)
public class MemberDietInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_id")
    private Long memberId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "activity_level")
    private String activityLevel;

    private String allergies;

    @Column(name = "dietary_preference")
    private String dietaryPreference;

    @Column(name = "food_preferences")
    private String foodPreferences;

    @Column(name = "meal_pattern")
    private String mealPattern;

    @Column(name = "meal_times")
    private String mealTimes;

    @Column(name = "special_requirements")
    private String specialRequirements;

    @Column(name = "food_avoidance")
    private String foodAvoidance;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public MemberDietInfo() {
        this.isDeleted = false; // 안전망
    }
}