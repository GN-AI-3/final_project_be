package com.example.final_project_be.domain.exercise_record.controller;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise.repository.ExerciseRepository;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordGroupedResponseDTO;
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

@RestController
@RequestMapping("/api/exercise_records")
@RequiredArgsConstructor
@Tag(name = "workout", description = "개인 운동 관련 API")
public class ExerciseRecordController {

    private static final Logger log = LoggerFactory.getLogger(ExerciseRecordController.class);
    
    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseRecordService exerciseRecordService;

    @PostMapping
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

    @GetMapping("/{exerciseRecordId}")
    @Transactional(readOnly = true)
    public ResponseEntity<ExerciseRecordResponseDTO> getExerciseRecord(@PathVariable Long id) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise record not found"));

        ExerciseRecordResponseDTO responseDTO = ExerciseRecordResponseDTO.from(exerciseRecord);
        return ResponseEntity.ok(responseDTO);
    }

    @PatchMapping
    @Transactional
    @Operation(summary = "운동 기록 수정", description = "회원 ID, 운동 ID, 날짜로 운동 기록을 찾아 recordData와 memoData를 수정합니다.")
    public ResponseEntity<ExerciseRecordResponseDTO> updateExerciseRecord(@RequestBody ExerciseRecordUpdateRequestDTO requestDTO) {
        ExerciseRecordResponseDTO responseDTO = exerciseRecordService.updateExerciseRecord(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/grouped")
    @Transactional(readOnly = true)
    @Operation(summary = "운동 기록 날짜별 조회", 
            description = "지정된 기간 내의 회원의 운동 기록을 날짜별로 그룹화하여 조회합니다.")
    public ResponseEntity<List<ExerciseRecordGroupedResponseDTO>> getExerciseRecordsGroupedByDate(
            @RequestParam Long memberId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endTime) {
        
        log.info("운동 기록 날짜별 조회 요청 - 회원 ID: {}, 시작일: {}, 종료일: {}", 
                memberId, startTime, endTime);

        List<ExerciseRecordGroupedResponseDTO> response = exerciseRecordService
                .getExerciseRecordsGroupedByDate(memberId, startTime, endTime);
        
        return ResponseEntity.ok(response);
    }
} 