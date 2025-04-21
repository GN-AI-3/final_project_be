package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.dto.MemberUpdateRequest;
import com.example.final_project_be.domain.food.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member API", description = "사용자 정보 관련 API")
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberGoalController {
    private final MemberService memberService;

    @Operation(summary = "사용자 정보 업데이트", description = "사용자의 목표와 성별 정보를 업데이트합니다.")
    @PutMapping("/update")
    public ResponseEntity<Void> updateMember(@RequestBody MemberUpdateRequest request) {
        memberService.updateMember(request);
        return ResponseEntity.ok().build();
    }
}