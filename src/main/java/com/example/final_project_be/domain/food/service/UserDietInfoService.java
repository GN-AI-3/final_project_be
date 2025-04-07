package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.entity.UserDietInfo;
import com.example.final_project_be.domain.food.repository.UserDietInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDietInfoService {

    private final UserDietInfoRepository userDietInfoRepository;

    @Autowired
    public UserDietInfoService(UserDietInfoRepository userDietInfoRepository) {
        this.userDietInfoRepository = userDietInfoRepository;
    }

    public Optional<UserDietInfo> getUserDietInfo(Long memberId) {
        return userDietInfoRepository.findByMember(memberId);
    }
}
