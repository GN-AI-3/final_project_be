package com.example.final_project_be.domain.trainer.service;

import com.example.final_project_be.domain.trainer.dto.TrainerDetailDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.domain.trainer.entity.Subscribe;
import com.example.final_project_be.domain.trainer.entity.Trainer;
import com.example.final_project_be.domain.trainer.enums.SubscriptionStatus;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
import com.example.final_project_be.props.JwtProps;
import com.example.final_project_be.security.CustomUserDetailService;
import com.example.final_project_be.security.TrainerDTO;
import com.example.final_project_be.util.JWTUtil;
import com.example.final_project_be.util.file.CustomFileUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void join(TrainerJoinRequestDTO request) {
        trainerRepository.findByEmail(request.getEmail())
                .ifPresent(trainer -> {
                    throw new IllegalArgumentException("이미 존재하는 아이디입니다!");
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

    @Override
    @Transactional
    public Boolean subscribeUpgrade(String email, String subscriptionType) {
        try {
            Trainer trainer = getEntity(email);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endDate = now.plusDays(30); // 30일 구독 기간

            if (trainer.getSubscribe() != null) {
                Subscribe existingSubscribe = trainer.getSubscribe();
                
                // 기존 구독이 있으면 구독 유형 변경 (start date는 유지, end date는 현재 시간으로부터 30일 후로 갱신)
                if ("BASIC".equals(subscriptionType)) {
                    existingSubscribe = Subscribe.builder()
                            .id(existingSubscribe.getId())
                            .name("BASIC")
                            .price("10000")
                            .management_person("10")
                            .startDate(existingSubscribe.getStartDate()) // 기존 시작일 유지
                            .endDate(endDate) // 구독 종료일 갱신
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else if ("STANDARD".equals(subscriptionType)) {
                    existingSubscribe = Subscribe.builder()
                            .id(existingSubscribe.getId())
                            .name("STANDARD")
                            .price("20000")
                            .management_person("20")
                            .startDate(existingSubscribe.getStartDate()) // 기존 시작일 유지
                            .endDate(endDate) // 구독 종료일 갱신
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else if ("PREMIUM".equals(subscriptionType)) {
                    existingSubscribe = Subscribe.builder()
                            .id(existingSubscribe.getId())
                            .name("PREMIUM")
                            .price("30000")
                            .management_person("30")
                            .startDate(existingSubscribe.getStartDate()) // 기존 시작일 유지
                            .endDate(endDate) // 구독 종료일 갱신
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else {
                    return false;
                }

                entityManager.merge(existingSubscribe);
            } else {
                Subscribe newSubscribe;

                if ("BASIC".equals(subscriptionType)) {
                    newSubscribe = Subscribe.builder()
                            .name("BASIC")
                            .price("10000")
                            .management_person("10")
                            .startDate(now) // 현재 시간으로 시작일 설정
                            .endDate(endDate) // 30일 후로 종료일 설정
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else if ("STANDARD".equals(subscriptionType)) {
                    newSubscribe = Subscribe.builder()
                            .name("STANDARD")
                            .price("20000")
                            .management_person("20")
                            .startDate(now) // 현재 시간으로 시작일 설정
                            .endDate(endDate) // 30일 후로 종료일 설정
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else if ("PREMIUM".equals(subscriptionType)) {
                    newSubscribe = Subscribe.builder()
                            .name("PREMIUM")
                            .price("30000")
                            .management_person("30")
                            .startDate(now) // 현재 시간으로 시작일 설정
                            .endDate(endDate) // 30일 후로 종료일 설정
                            .status(SubscriptionStatus.ACTIVE) // 상태 활성화
                            .trainer(trainer)
                            .build();
                } else {
                    return false;
                }

                entityManager.persist(newSubscribe);
            }
            
            return true;
        } catch (Exception e) {
            log.error("Error upgrading subscription: ", e);
            return false;
        }
    }

    @Override
    @Transactional
    public void checkAndUpdateExpiredSubscriptions() {
        LocalDateTime now = LocalDateTime.now();
        
        log.info("Checking for expired subscriptions at {}", now);
        
        // 모든 트레이너 조회
        List<Trainer> trainers = trainerRepository.findAll();
        
        for (Trainer trainer : trainers) {
            Subscribe subscribe = trainer.getSubscribe();
            
            // 구독이 있고, 활성 상태이며, 종료일이 현재 시간보다 이전인 경우
            if (subscribe != null && 
                SubscriptionStatus.ACTIVE.equals(subscribe.getStatus()) && 
                subscribe.getEndDate() != null && 
                subscribe.getEndDate().isBefore(now)) {
                
                log.info("Subscription expired for trainer: {}", trainer.getEmail());
                
                // 구독 상태를 만료로 변경
                Subscribe expiredSubscribe = Subscribe.builder()
                        .id(subscribe.getId())
                        .name(subscribe.getName())
                        .price(subscribe.getPrice())
                        .management_person(subscribe.getManagement_person())
                        .startDate(subscribe.getStartDate())
                        .endDate(subscribe.getEndDate())
                        .status(SubscriptionStatus.EXPIRED) // 상태를 만료로 변경
                        .trainer(trainer)
                        .build();
                
                entityManager.merge(expiredSubscribe);
            }
        }
    }

    @Override
    @Transactional
    public Boolean cancelSubscription(String email) {
        try {
            Trainer trainer = getEntity(email);
            Subscribe subscribe = trainer.getSubscribe();
            
            if (subscribe != null && SubscriptionStatus.ACTIVE.equals(subscribe.getStatus())) {
                log.info("Canceling subscription for trainer: {}", trainer.getEmail());
                
                // 구독 상태를 취소로 변경
                Subscribe canceledSubscribe = Subscribe.builder()
                        .id(subscribe.getId())
                        .name(subscribe.getName())
                        .price(subscribe.getPrice())
                        .management_person(subscribe.getManagement_person())
                        .startDate(subscribe.getStartDate())
                        .endDate(subscribe.getEndDate())
                        .status(SubscriptionStatus.CANCELED) // 상태를 취소로 변경
                        .trainer(trainer)
                        .build();
                
                entityManager.merge(canceledSubscribe);
                return true;
            } else {
                log.warn("No active subscription found for trainer: {}", trainer.getEmail());
                return false;
            }
        } catch (Exception e) {
            log.error("Error canceling subscription: ", e);
            return false;
        }
    }
} 