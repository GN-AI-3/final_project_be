package com.example.final_project_be.domain.food.service;

import com.example.final_project_be.domain.food.dto.UserInfoResponseDTO;
import com.example.final_project_be.domain.food.repository.UserDietInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@RequiredArgsConstructor
@Service
public class UserDietInfoService {

    private final UserDietInfoRepository userDietInfoRepository;



    public Optional<UserInfoResponseDTO> getUserDietInfo(Long memberId) {
        return userDietInfoRepository.findByMember(memberId);
    }
}
