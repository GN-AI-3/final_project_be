package com.example.final_project_be.domain.report.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.domain.pt.repository.PtContractRepository;
import com.example.final_project_be.domain.report.dto.ReportContentDTO;
import com.example.final_project_be.domain.report.dto.ReportResponseDTO;
import com.example.final_project_be.domain.report.entity.Report;
import com.example.final_project_be.domain.report.repository.ReportRepository;
import com.example.final_project_be.domain.report.service.ReportService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "report", description = "보고서 관련 API")
public class ReportController {

    private final ReportRepository reportRepository;
    private final PtContractRepository ptContractRepository;
    private final ReportService reportService;

    @PostMapping("/{ptContractId}")
    @Transactional
    @Operation(summary = "보고서 생성", description = "PT 계약 ID와 보고서 내용을 받아 새로운 보고서를 생성합니다.")
    public ResponseEntity<ReportResponseDTO> createReport(
            @PathVariable Long ptContractId,
            @RequestBody ReportContentDTO contentDTO) {
        PtContract ptContract = ptContractRepository.findById(ptContractId)
                .orElseThrow(() -> new RuntimeException("PT 계약을 찾을 수 없습니다. ID: " + ptContractId));

        Report report = Report.builder()
                .ptContract(ptContract)
                .exerciseReport(contentDTO.getExerciseReport())
                .dietReport(contentDTO.getDietReport())
                .inbodyReport(contentDTO.getInbodyReport())
                .build();

        Report savedReport = reportRepository.save(report);
        return ResponseEntity.ok(ReportResponseDTO.from(savedReport));
    }

    @PostMapping("/fast-api/{ptContractId}")
    @Operation(summary = "FastAPI 보고서 생성", description = "FastAPI 서버를 통해 보고서를 생성합니다.")
    public ResponseEntity<Map<String, Object>> createReportWithFastApi(@PathVariable Long ptContractId) {
        Map<String, Object> response = reportService.callFastApiReport(ptContractId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest/{ptContractId}")
    @Operation(summary = "최근 보고서 조회", description = "PT 계약 ID로 최근 보고서와 첫 보고서를 조회합니다.")
    public ResponseEntity<List<ReportResponseDTO>> getLatestReports(@PathVariable Long ptContractId) {
        List<Report> reports = reportRepository.findLatestAndOldestByPtContractId(ptContractId);
        List<ReportResponseDTO> response = reports.stream()
                .map(ReportResponseDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
} 