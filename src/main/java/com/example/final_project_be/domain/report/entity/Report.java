package com.example.final_project_be.domain.report.entity;

import org.hibernate.annotations.Type;

import com.example.final_project_be.domain.pt.entity.PtContract;
import com.example.final_project_be.entity.BaseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "report")
public class Report extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pt_contract_id", nullable = false)
    private PtContract ptContract;

    @Column(name = "exercise_report", columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private JsonNode exerciseReport;

    @Column(name = "diet_report", columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private JsonNode dietReport;

    @Column(name = "overall_report", columnDefinition = "jsonb")
    @Type(value = JsonBinaryType.class)
    private JsonNode overallReport;
} 