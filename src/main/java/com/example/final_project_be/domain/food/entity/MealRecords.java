package com.example.final_project_be.domain.food.entity;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "meal_records")
public class MealRecords extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @Column(name = "food_name", nullable = false, length = 255)
    private String foodName;

    @Column(name = "portion", nullable = false, precision = 5, scale = 2)
    private Double portion;

    @Column(name = "unit", nullable = false, length = 10)
    private String unit;

    @Column(name = "meal_date", nullable = false)
    private LocalDate mealDate;

    @Column(name = "meal_time", nullable = false)
    private LocalTime mealTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", nullable = false, length = 10)
    private MealType mealType;

    @Column(name = "calories", nullable = false, precision = 7, scale = 2)
    private Double calories;

    @Column(name = "protein", nullable = false, precision = 5, scale = 2)
    private Double protein;

    @Column(name = "carbs", nullable = false, precision = 5, scale = 2)
    private Double carbs;

    @Column(name = "fat", nullable = false, precision = 5, scale = 2)
    private Double fat;

    public enum MealType {
        아침, 점심, 저녁, 간식
    }
}
