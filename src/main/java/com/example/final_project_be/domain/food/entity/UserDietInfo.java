package com.example.final_project_be.domain.food.entity;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_diet_info")
public class UserDietInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "activity_level", length = 50)
    private String activityLevel;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "dietary_preference", length = 50)
    private String dietaryPreference;

    @Column(name = "meal_pattern", length = 50)
    private String mealPattern;

    @Column(name = "meal_times", columnDefinition = "TEXT")
    private String mealTimes;

    @Column(name = "food_preferences", columnDefinition = "TEXT")
    private String foodPreferences;

    @Column(name = "special_requirements", columnDefinition = "TEXT")
    private String specialRequirements;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
