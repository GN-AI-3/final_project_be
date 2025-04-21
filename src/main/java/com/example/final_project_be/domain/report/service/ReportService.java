package com.example.final_project_be.domain.report.service;

import java.util.Map;

public interface ReportService {
    Map<String, Object> callFastApiReport(Long ptContractId);
} 