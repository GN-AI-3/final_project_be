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
@Table(name = "food_nutrition")
public class FoodNutrition extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "name", length = 255, unique = true, nullable = false)
    private String Name;

    @Column(name = "calories", nullable = false)
    private Float Calories;

    @Column(name = "protein", nullable = false)
    private Float Protein;

    @Column(name = "carbs", nullable = false)
    private Float Carbs;

    @Column(name = "fat", nullable = false)
    private Float Fat;
}
