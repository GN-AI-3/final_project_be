package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.trainer.dto.TrainerDetailDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.security.TrainerDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.Map;

public interface TrainerService {
    void join(@Valid TrainerJoinRequestDTO trainerJoinRequestDTO);

    Map<String, Object> login(@NotBlank(message = "이메일을 입력해주세요") String email, @NotBlank(message = "패스워드를 입력해주세요") String password);

    Trainer getEntity(String email);

    TrainerDetailDTO getMyInfo(String email);
    
    Boolean checkEmail(String email);

    default TrainerDTO entityToDTO(Trainer trainer) {
        return new TrainerDTO(
                trainer.getEmail(),
                trainer.getPassword(),
                trainer.getPhone(),
                trainer.getName(),
                trainer.getUserType(),
                trainer.getCareer(),
                trainer.getSpeciality()
        );
    }
} 