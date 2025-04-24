package com.example.final_project_be.domain.exercise_record.service;

import com.example.final_project_be.domain.exercise.entity.Exercise;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordGroupedResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordResponseDTO;
import com.example.final_project_be.domain.exercise_record.dto.ExerciseRecordUpdateRequestDTO;
import com.example.final_project_be.domain.exercise_record.entity.ExerciseRecord;
import com.example.final_project_be.domain.exercise_record.repository.ExerciseRecordRepository;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ExerciseRecordServiceImpl implements ExerciseRecordService {

    private static final Logger log = LoggerFactory.getLogger(ExerciseRecordServiceImpl.class);

    private final ExerciseRecordRepository exerciseRecordRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ExerciseRecord saveExerciseRecord(
            Member member,
            Exercise exercise,
            LocalDate date,
            JsonNode recordData,
            JsonNode memoData
    ) {
        ExerciseRecord exerciseRecord = ExerciseRecord.builder()
                .member(member)
                .exercise(exercise)
                .date(date)
                .recordData(recordData)
                .memoData(memoData)
                .build();

        return exerciseRecordRepository.save(exerciseRecord);
    }

    @Override
    public Optional<ExerciseRecord> findById(Long id) {
        return exerciseRecordRepository.findById(id);
    }

    @Override
    @Transactional
    public ExerciseRecordResponseDTO updateExerciseRecord(ExerciseRecordUpdateRequestDTO requestDTO) {
        ExerciseRecord exerciseRecord = exerciseRecordRepository
                .findByMemberIdAndExerciseIdAndDate(
                        requestDTO.getMemberId(),
                        requestDTO.getExerciseId(),
                        requestDTO.getDate()
                )
                .orElseThrow(() -> new RuntimeException("Exercise record not found"));

        if (requestDTO.getRecordData() != null) {
            exerciseRecord.setRecordData(requestDTO.getRecordData());
        }

        if (requestDTO.getMemoData() != null) {
            exerciseRecord.setMemoData(requestDTO.getMemoData());
        }

        ExerciseRecord updatedRecord = exerciseRecordRepository.save(exerciseRecord);
        return ExerciseRecordResponseDTO.from(updatedRecord);
    }

    @Override
    public List<ExerciseRecordGroupedResponseDTO> getExerciseRecordsGroupedByDate(
            Long memberId, LocalDate startTime, LocalDate endTime) {
        
        log.info("운동 기록 날짜별 조회 시작 - 회원 ID: {}, 시작일: {}, 종료일: {}", 
                memberId, startTime, endTime);

        // 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + memberId));

        // 기간 내의 운동 기록 조회
        List<ExerciseRecord> records = exerciseRecordRepository
                .findByMemberIdAndDateBetween(memberId, startTime, endTime);

        // 날짜별로 그룹화
        Map<LocalDate, List<ExerciseRecord>> groupedByDate = records.stream()
                .collect(Collectors.groupingBy(ExerciseRecord::getDate));

        // DTO로 변환
        return groupedByDate.entrySet().stream()
                .map(entry -> {
                    ExerciseRecordGroupedResponseDTO dto = new ExerciseRecordGroupedResponseDTO();
                    dto.setDate(entry.getKey().toString());
                    
                    List<ExerciseRecordGroupedResponseDTO.ExerciseRecordDetailDTO> details = entry.getValue().stream()
                            .map(record -> {
                                ExerciseRecordGroupedResponseDTO.ExerciseRecordDetailDTO detail = 
                                        new ExerciseRecordGroupedResponseDTO.ExerciseRecordDetailDTO();
                                detail.setExerciseId(record.getExercise().getId());
                                detail.setExerciseName(record.getExercise().getName());
                                detail.setRecordData(record.getRecordData());
                                detail.setMemoData(record.getMemoData());
                                return detail;
                            })
                            .collect(Collectors.toList());
                    
                    dto.setRecords(details);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExerciseRecordResponseDTO> getExerciseRecordsByMemberId(Long memberId) {
        log.info("회원의 모든 운동 기록 조회 시작 - 회원 ID: {}", memberId);
        
        // 회원 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + memberId));
        
        // 회원의 모든 운동 기록 조회
        List<ExerciseRecord> allRecords = exerciseRecordRepository.findByMemberId(memberId);
        
        // DTO로 변환하여 반환
        return allRecords.stream()
                .map(ExerciseRecordResponseDTO::from)
                .collect(Collectors.toList());
    }
} 