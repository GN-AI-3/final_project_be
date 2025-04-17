package com.example.final_project_be.domain.consult.entity;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@DynamicUpdate
@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consult")
public class Consult extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_contract_id")
    private PtContract ptContract;

    // 1. 기본 정보
    @Column(length = 100)
    @Setter
    private String job;

    @Column(length = 200)
    @Setter
    private String lifestyle;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    @Setter
    private String medicalHistory;

    @Column(name = "training_goal", length = 200)
    @Setter
    private String trainingGoal;

    @Column(name = "goal_deadline")
    @Setter
    private LocalDate goalDeadline;

    @Column(name = "consultation_date")
    @Setter
    private LocalDate consultationDate;

    @Column(name = "consultation_channel", length = 50)
    @Setter
    private String consultationChannel;

    @Column(name = "consultant_name", length = 100)
    @Setter
    private String consultantName;

    @Column(name = "consultation_notes", columnDefinition = "TEXT")
    @Setter
    private String consultationNotes;

    // 2. 운동 정보
    @Column(name = "has_experience")
    @Setter
    private Boolean hasExperience;

    @Column(name = "exercise_experience", columnDefinition = "TEXT")
    @Setter
    private String exerciseExperience;

    @Column(name = "weekly_workout_frequency")
    @Setter
    private Integer weeklyWorkoutFrequency;

    @ElementCollection
    @CollectionTable(name = "consult_preferred_exercises", joinColumns = @JoinColumn(name = "consult_id"))
    @Column(name = "preferred_exercises")
    @Builder.Default
    private List<String> preferredExercises = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "consult_disliked_exercises", joinColumns = @JoinColumn(name = "consult_id"))
    @Column(name = "disliked_exercises")
    @Builder.Default
    private List<String> dislikedExercises = new ArrayList<>();

    @Column(name = "weak_points_or_pain", columnDefinition = "TEXT")
    @Setter
    private String weakPointsOrPain;

    @ElementCollection
    @CollectionTable(name = "consult_body_concerns", joinColumns = @JoinColumn(name = "consult_id"))
    @Column(name = "body_concerns")
    @Builder.Default
    private List<String> bodyConcerns = new ArrayList<>();

    // 3. 식단 정보
    @Column(name = "needs_diet_plan")
    @Setter
    private Boolean needsDietPlan;

    // 4. 일정 정보
    @ElementCollection
    @CollectionTable(name = "consult_preferred_days", joinColumns = @JoinColumn(name = "consult_id"))
    @Column(name = "preferred_days")
    @Builder.Default
    private List<String> preferredDays = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "consult_preferred_times", joinColumns = @JoinColumn(name = "consult_id"))
    @Column(name = "preferred_times")
    @Builder.Default
    private List<String> preferredTimes = new ArrayList<>();

    @Column(name = "available_sessions_per_week")
    @Setter
    private Integer availableSessionsPerWeek;

    @Column(name = "distance_to_gym", length = 100)
    @Setter
    private String distanceToGym;
}
