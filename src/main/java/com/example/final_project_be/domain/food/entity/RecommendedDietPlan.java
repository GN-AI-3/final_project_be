package com.example.final_project_be.domain.food.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "recommended_diet_plans")
public class RecommendedDietPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recommendedDietPlanId;

    @Column(name = "member_id")
    private Long memberId;

    @CreatedDate
    @Column(name = "created_datetime")
    private LocalDateTime createdDatetime;

    @Column(name = "plan_day")
    private String planDay;

    @Column(name = "breakfast_plan")
    private String breakfastPlan;

    @Column(name = "lunch_plan")
    private String lunchPlan;

    @Column(name = "dinner_plan")
    private String dinnerPlan;

    @Column(name = "plan_summary")
    private String planSummary;

    @Column(name = "plan_scope")
    private String planScope;
}