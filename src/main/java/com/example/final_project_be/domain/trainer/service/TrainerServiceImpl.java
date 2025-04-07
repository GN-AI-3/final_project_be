package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.trainer.dto.TrainerDetailDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.props.JwtProps;
import com.example.final_project_be.security.CustomUserDetailService;
import com.example.final_project_be.security.TrainerDTO;
import com.example.final_project_be.util.JWTUtil;
import com.example.final_project_be.util.file.CustomFileUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final TrainerRepository trainerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomFileUtil fileUtil;
    private final CustomUserDetailService customUserDetailService;

    @Override
    public void join(TrainerJoinRequestDTO request) {
        trainerRepository.findByEmail(request.getEmail())
                .ifPresent(trainer -> {
                    throw new IllegalArgumentException("이미 존재하는 트레이너입니다!");
                });

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Trainer trainer = Trainer.from(request);
        trainerRepository.save(trainer);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<String, Object> login(String email, String password) {
        UserDetails userDetails;
        try {
            // TRAINER 타입으로 사용자 로드 시도
            userDetails = customUserDetailService.loadUserByUsernameAndType(email, "TRAINER");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("해당 이메일로 등록된 트레이너가 없습니다.");
        }
        
        TrainerDTO trainerDTO = (TrainerDTO) userDetails;
        log.info("email : {}, password : {}", email, password);

        if(!passwordEncoder.matches(password, trainerDTO.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        Map<String, Object> trainerClaims = trainerDTO.getClaims();

        String accessToken = jwtUtil.generateToken(trainerClaims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(trainerClaims, jwtProps.getRefreshTokenExpirationPeriod());

        trainerClaims.put("accessToken", accessToken);
        trainerClaims.put("refreshToken", refreshToken);

        return trainerClaims;
    }

    @Override
    public Trainer getEntity(String email) {
        return trainerRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 트레이너가 없습니다. email: " + email));
    }

    @Transactional(readOnly = true)
    @Override
    public TrainerDetailDTO getMyInfo(String email) {
        Trainer trainer = getEntity(email);
        return TrainerDetailDTO.from(trainer);
    }

    @Override
    public Boolean checkEmail(String email) {
        return trainerRepository.existsByEmail(email);
    }
} 