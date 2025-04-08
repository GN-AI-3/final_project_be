package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.dto.UserInfoResponseDTO;
import com.example.final_project_be.domain.food.entity.UserDietInfo;
import com.example.final_project_be.domain.food.service.UserDietInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/userdietinfo")
public class UserDietInfoController {

    private final UserDietInfoService userDietInfoService;


    // 특정 회원의 식이 정보를 조회
    @GetMapping("/{memberId}")
    public Optional<UserInfoResponseDTO> getUserDietInfo(@PathVariable Long memberId) {
        return userDietInfoService.getUserDietInfo(memberId);
    }
}
