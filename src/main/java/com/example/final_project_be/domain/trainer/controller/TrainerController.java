package com.example.final_project_be.domain.trainer.controller;

import com.example.final_project_be.domain.trainer.dto.SubscribeRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerDetailDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerJoinRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerLoginRequestDTO;
import com.example.final_project_be.domain.trainer.dto.TrainerLoginResponseDTO;
import com.example.final_project_be.domain.trainer.service.TrainerService;
import com.example.final_project_be.props.JwtProps;
import com.example.final_project_be.security.TrainerDTO;
import com.example.final_project_be.util.CookieUtil;
import com.example.final_project_be.util.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.example.final_project_be.util.TimeUtil.checkTime;

@Slf4j
@RestController
@RequestMapping("/api/trainer")
@RequiredArgsConstructor
@Tag(name = "trainer - api", description = "로그인, 회원가입, 트레이너 정보 조회 / 수정 등 trainer 관련")
public class TrainerController {

    private final TrainerService trainerService;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;


    @Operation(summary = "트레이너 회원가입 및 정보 db 저장 api")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody TrainerJoinRequestDTO joinRequestDTO) {
        log.info("Trainer Join request: {}", joinRequestDTO);
        
        // 목록 필드들이 null이면 빈 목록으로 초기화
        if (joinRequestDTO.getCertifications() == null) {
            joinRequestDTO.setCertifications(new ArrayList<>());
            log.info("Certifications was null, initialized to empty list");
        } else if (joinRequestDTO.getCertifications().isEmpty()) {
            log.info("Certifications was empty list");
        }
        
        if (joinRequestDTO.getSpecialities() == null) {
            joinRequestDTO.setSpecialities(new ArrayList<>());
            log.info("Specialities was null, initialized to empty list");
        } else if (joinRequestDTO.getSpecialities().isEmpty()) {
            log.info("Specialities was empty list");
        }
        
        // 로깅을 통해 값이 제대로 설정되었는지 확인
        log.info("After initialization - Certifications: {}, Specialities: {}", 
                joinRequestDTO.getCertifications(), 
                joinRequestDTO.getSpecialities());
        
        trainerService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "트레이너 로그인, 인증처리 api")
    @PostMapping("/login")
    public ResponseEntity<TrainerLoginResponseDTO> login(@Valid @RequestBody
                                                        TrainerLoginRequestDTO loginRequestDTO,
                                                        HttpServletResponse response) {
        log.info("Trainer Login request: {}", loginRequestDTO);
        Map<String, Object> loginClaims = trainerService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        String refreshToken = loginClaims.get("refreshToken").toString();
        String accessToken = loginClaims.get("accessToken").toString();

        CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());

        TrainerLoginResponseDTO loginResponseDTO = TrainerLoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .name(loginClaims.get("name").toString())
                .userType(loginClaims.getOrDefault("userType", "TRAINER").toString())
                .id(((Number) loginClaims.get("id")).longValue())
                .accessToken(accessToken)
                .career(loginClaims.getOrDefault("career", "").toString())
                .certifications((List<String>) loginClaims.getOrDefault("certifications", Collections.emptyList()))
                .specialities((List<String>) loginClaims.getOrDefault("specialities", Collections.emptyList()))
                .build();

        log.info("Trainer Login response: {}", loginResponseDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("Trainer Logout");
        CookieUtil.removeTokenCookie(response, "refreshToken");
        return ResponseEntity.ok("logout success!");
    }


    @Operation(summary = "토큰 갱신", description = "refreshToken으로 새로운 accessToken을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrainerLoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 refreshToken"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })


    @GetMapping("/refresh")
    public ResponseEntity<TrainerLoginResponseDTO> refresh(
            @Parameter(description = "refreshToken 쿠키", required = true)
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        log.info("refresh refreshToken: {}", refreshToken);

        Map<String, Object> loginClaims = jwtUtil.validateToken(refreshToken);
        log.info("RefreshToken loginClaims: {}", loginClaims);

        String newAccessToken = jwtUtil.generateToken(loginClaims, jwtProps.getAccessTokenExpirationPeriod());
        String newRefreshToken = jwtUtil.generateToken(loginClaims, jwtProps.getRefreshTokenExpirationPeriod());

        // refreshToken 만료시간이 1시간 이하로 남았다면, 새로 발급
        if (checkTime((Integer) loginClaims.get("exp"))) {
            // 새로 발급
            CookieUtil.setTokenCookie(response, "refreshToken", newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod());
        } else {
            // 만료시간이 1시간 이상이면, 기존 refreshToken 그대로
            CookieUtil.setNewRefreshTokenCookie(response, "refreshToken", refreshToken);
        }

        TrainerLoginResponseDTO loginResponseDTO = TrainerLoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .name(loginClaims.get("name").toString())
                .userType(loginClaims.getOrDefault("userType", "TRAINER").toString())
                .id(((Number) loginClaims.get("id")).longValue())
                .accessToken(newAccessToken)
                .career(loginClaims.getOrDefault("career", "").toString())
                .certifications((List<String>) loginClaims.getOrDefault("certifications", Collections.emptyList()))
                .specialities((List<String>) loginClaims.getOrDefault("specialities", Collections.emptyList()))
                .build();

        log.info("refresh loginResponseDTO: {}", loginResponseDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }


    // 회원가입시, 아이디(email) 중복확인 -> false, true 반환
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        log.info("checkEmail email: {}", email);
        return ResponseEntity.ok(trainerService.checkEmail(email));
    }


    @GetMapping("/me")
    public TrainerDetailDTO getMyInfo(@AuthenticationPrincipal TrainerDTO trainer) {
        log.info("getMyInfo: {}", trainer);
        if(trainer == null) {
            return TrainerDetailDTO.builder().build();
        }
        return trainerService.getMyInfo(trainer.getEmail());
    }


    @Operation(summary = "트레이너 구독 업그레이드 api")
    @PostMapping("/subscribe_upgrade")
    public ResponseEntity<String> subscribeUpgrade(
            @AuthenticationPrincipal TrainerDTO trainer,
            @Valid @RequestBody SubscribeRequestDTO subscribeRequestDTO) {
        log.info("subscribeUpgrade: {}, subscriptionType: {}", trainer, subscribeRequestDTO.getSubscriptionType());
        if(trainer == null) {
            return ResponseEntity.badRequest().body("인증된 트레이너 정보가 없습니다.");
        }
        
        Boolean result = trainerService.subscribeUpgrade(trainer.getEmail(), subscribeRequestDTO.getSubscriptionType());
        
        if (result) {
            return ResponseEntity.ok("구독 업그레이드가 성공적으로 완료되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("구독 업그레이드에 실패했습니다. 유효한 구독 유형인지 확인해주세요.");
        }
    }

    @Operation(summary = "트레이너 구독 취소 api")
    @PostMapping("/subscribe_cancel")
    public ResponseEntity<String> subscribeCancel(@AuthenticationPrincipal TrainerDTO trainer) {
        log.info("subscribeCancel: {}", trainer);
        if(trainer == null) {
            return ResponseEntity.badRequest().body("인증된 트레이너 정보가 없습니다.");
        }
        
        Boolean result = trainerService.cancelSubscription(trainer.getEmail());
        
        if (result) {
            return ResponseEntity.ok("구독이 성공적으로 취소되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("구독 취소에 실패했습니다. 활성화된 구독이 있는지 확인해주세요.");
        }
    }

} 