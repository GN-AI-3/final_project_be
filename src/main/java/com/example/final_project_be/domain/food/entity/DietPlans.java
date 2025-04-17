package com.example.final_project_be.domain.food.entity;

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
@Table(name = "diet_plans")
public class DietPlans extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "diet_type", length = 50)
    private String DietType;

    @Column(name = "breakfast", columnDefinition = "TEXT")
    private String Breakfast;

    @Column(name = "lunch", columnDefinition = "TEXT")
    private String Lunch;

    @Column(name = "dinner", columnDefinition = "TEXT")
    private String Dinner;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_gender", length = 3, nullable = false)
    private Gender UserGender;

    public enum Gender {
        남성, 여성
    }
}
