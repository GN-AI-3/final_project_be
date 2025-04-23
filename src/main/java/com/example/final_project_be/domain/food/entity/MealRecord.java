package com.example.final_project_be.domain.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "meal_records")
@EntityListeners(AuditingEntityListener.class)
public class MealRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealRecordsId;

    @Column(name = "member_id")
    private Long memberId;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;

    @Column(name = "food_name")
    private String foodName;

    private Double portion;                  

    private String unit;

    @Column(name = "meal_date")
    private LocalDate mealDate;

    @Column(name = "meal_time")
    private LocalTime mealTime;

    @Column(name = "meal_type")
    private String mealType;

    private Double calories;

    private Double protein;

    private Double carbs;

    private Double fat;

    private Double estimated_grams;
}