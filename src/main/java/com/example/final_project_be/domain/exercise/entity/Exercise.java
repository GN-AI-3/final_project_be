package com.example.final_project_be.domain.exercise.entity;

import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Table(name = "exercise")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int reps;
    private int distance;

    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ExerciseRecord> records = new ArrayList<>();

//    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL)
//    @Builder.Default
//    private List<CategoryBridge> categoryBridges = new ArrayList<>();


}
