package com.example.final_project_be.domain.food.controller;

import com.example.final_project_be.domain.food.entity.UserDietInfo;
import com.example.final_project_be.domain.food.service.UserDietInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/userdietinfo")
public class UserDietInfoController {

    private final UserDietInfoService userDietInfoService;

    @Autowired
    public UserDietInfoController(UserDietInfoService userDietInfoService) {
        this.userDietInfoService = userDietInfoService;
    }

    // 특정 회원의 식이 정보를 조회
    @GetMapping("/{memberId}")
    public Optional<UserDietInfo> getUserDietInfo(@PathVariable Long memberId) {
        return userDietInfoService.getUserDietInfo(memberId);
    }
}
