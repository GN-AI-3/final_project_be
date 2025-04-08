package com.example.final_project_be.domain.exercise_record.controller;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordRequestDTO;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.exercise_record.repository.ExerciseRecordRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise_records")
@RequiredArgsConstructor
public class ExerciseRecordController {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;

    @PostMapping("/save_exercise_record")
    public ResponseEntity<ExerciseRecord> createExerciseRecord(@RequestBody ExerciseRecordRequestDTO requestDTO) {
        Member member = memberRepository.findById(requestDTO.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        
        Exercise exercise = exerciseRepository.findById(requestDTO.getExerciseId())
                .orElseThrow(() -> new RuntimeException("Exercise not found"));

        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
                .member(member)
                .exercise(exercise)
                .date(requestDTO.getDate())
                .recordData(requestDTO.getRecordData())
                .memoData(requestDTO.getMemoData())
                .build();

        return ResponseEntity.ok(exerciseRecordRepository.save(exerciseRecord));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExerciseRecord> getExerciseRecord(@PathVariable Long id) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise record not found"));
        return ResponseEntity.ok(exerciseRecord);
    }
} 