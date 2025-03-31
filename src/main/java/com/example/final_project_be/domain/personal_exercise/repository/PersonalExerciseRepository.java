package com.example.final_project_be.domain.personal_exercise.repository;

import com.example.final_project_be.domain.personal_exercise.entity.PersonalExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonalExerciseRepository extends JpaRepository<PersonalExercise, Integer> {
}
