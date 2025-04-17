package com.example.final_project_be.domain.exercise_record.controller;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordRequestDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordUpdateRequestDTO;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.exercise_record.repository.ExerciseRecordRepository;
import com.example.final_project_be.domain.exercise_record.service.ExerciseRecordService;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercise_records")
@RequiredArgsConstructor
@Tag(name = "workout", description = "개인 운동 관련 API")
public class ExerciseRecordController {

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRecordService exerciseRecordService;

    @PostMapping("/save_exercise_record")
    @Transactional
    public ResponseEntity<ExerciseRecordResponseDTO> createExerciseRecord(@RequestBody ExerciseRecordRequestDTO requestDTO) {
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

        ExerciseRecord savedRecord = exerciseRecordRepository.save(exerciseRecord);

        // DTO로 변환하여 반환
        ExerciseRecordResponseDTO responseDTO = ExerciseRecordResponseDTO.from(savedRecord);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<ExerciseRecordResponseDTO> getExerciseRecord(@PathVariable Long id) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise record not found"));

        ExerciseRecordResponseDTO responseDTO = ExerciseRecordResponseDTO.from(exerciseRecord);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping("/update")
    @Transactional
    @Operation(summary = "운동 기록 수정", description = "회원 ID, 운동 ID, 날짜로 운동 기록을 찾아 recordData와 memoData를 수정합니다.")
    public ResponseEntity<ExerciseRecordResponseDTO> updateExerciseRecord(@RequestBody ExerciseRecordUpdateRequestDTO requestDTO) {
        ExerciseRecordResponseDTO responseDTO = exerciseRecordService.updateExerciseRecord(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
} 