package com.example.final_project_be.domain.consult.entity;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DynamicUpdate
@SuperBuilder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "consult")
public class Consult extends BaseEntity {
    
    private static final String DELIMITER = ";;";
    
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

    @Column(name = "preferred_exercises", columnDefinition = "TEXT")
    @Setter
    private String preferredExercisesStr;

    @Column(name = "disliked_exercises", columnDefinition = "TEXT")
    @Setter
    private String dislikedExercisesStr;

    @Column(name = "weak_points_or_pain", columnDefinition = "TEXT")
    @Setter
    private String weakPointsOrPain;

    @Column(name = "body_concerns", columnDefinition = "TEXT")
    @Setter
    private String bodyConcernsStr;

    // 3. 식단 정보
    @Column(name = "needs_diet_plan")
    @Setter
    private Boolean needsDietPlan;

    // 4. 일정 정보
    @Column(name = "preferred_days", columnDefinition = "TEXT")
    @Setter
    private String preferredDaysStr;

    @Column(name = "preferred_times", columnDefinition = "TEXT")
    @Setter
    private String preferredTimesStr;

    @Column(name = "available_sessions_per_week")
    @Setter
    private Integer availableSessionsPerWeek;

    @Column(name = "distance_to_gym", length = 100)
    @Setter
    private String distanceToGym;
    
    // === String ↔ List 변환 메서드 ===
    
    /**
     * 선호하는 운동 목록을 리스트로 변환하여 반환합니다.
     */
    @Transient
    public List<String> getPreferredExercises() {
        return stringToList(preferredExercisesStr);
    }
    
    /**
     * 선호하는 운동 목록을 설정합니다.
     */
    public void setPreferredExercises(List<String> preferredExercises) {
        this.preferredExercisesStr = listToString(preferredExercises);
    }
    
    /**
     * 싫어하는 운동 목록을 리스트로 변환하여 반환합니다.
     */
    @Transient
    public List<String> getDislikedExercises() {
        return stringToList(dislikedExercisesStr);
    }
    
    /**
     * 싫어하는 운동 목록을 설정합니다.
     */
    public void setDislikedExercises(List<String> dislikedExercises) {
        this.dislikedExercisesStr = listToString(dislikedExercises);
    }
    
    /**
     * 신체 고민 목록을 리스트로 변환하여 반환합니다.
     */
    @Transient
    public List<String> getBodyConcerns() {
        return stringToList(bodyConcernsStr);
    }
    
    /**
     * 신체 고민 목록을 설정합니다.
     */
    public void setBodyConcerns(List<String> bodyConcerns) {
        this.bodyConcernsStr = listToString(bodyConcerns);
    }
    
    /**
     * 선호하는 요일 목록을 리스트로 변환하여 반환합니다.
     */
    @Transient
    public List<String> getPreferredDays() {
        return stringToList(preferredDaysStr);
    }
    
    /**
     * 선호하는 요일 목록을 설정합니다.
     */
    public void setPreferredDays(List<String> preferredDays) {
        this.preferredDaysStr = listToString(preferredDays);
    }
    
    /**
     * 선호하는 시간 목록을 리스트로 변환하여 반환합니다.
     */
    @Transient
    public List<String> getPreferredTimes() {
        return stringToList(preferredTimesStr);
    }
    
    /**
     * 선호하는 시간 목록을 설정합니다.
     */
    public void setPreferredTimes(List<String> preferredTimes) {
        this.preferredTimesStr = listToString(preferredTimes);
    }
    
    // === 유틸리티 메서드 ===
    
    /**
     * 문자열 리스트를 구분자로 연결된 하나의 문자열로 변환합니다.
     */
    private String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream()
                .filter(item -> item != null && !item.isEmpty())
                .map(item -> item.replace(DELIMITER, ",")) // 구분자가 포함된 경우 대체
                .collect(Collectors.joining(DELIMITER));
    }
    
    /**
     * 구분자로 연결된 문자열을 문자열 리스트로 변환합니다.
     */
    private List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(DELIMITER))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }
}
