package com.example.final_project_be.domain.member.controller;

import com.example.final_project_be.domain.member.dto.JoinRequestDTO;
import com.example.final_project_be.domain.member.dto.LoginRequestDTO;
import com.example.final_project_be.domain.member.dto.LoginResponseDTO;
import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.service.MemberService;
import com.example.final_project_be.props.JwtProps;
import com.example.final_project_be.security.MemberDTO;
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
import java.util.List;
import java.util.Map;

import static com.example.final_project_be.util.TimeUtil.checkTime;

@Slf4j
@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
@Tag(name = "member - api", description = "로그인, 회원가입, 회원정보 조회 / 수정 등 member 관련 ")
public class MemberController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;

    @Operation(summary = "회원가입 및 회원정보 db 에 저장 api")
    @PostMapping("/join")
    public ResponseEntity<?> join(@Valid @RequestBody JoinRequestDTO joinRequestDTO) {
        log.info("Join request: {}", joinRequestDTO);
        
        // goal이 null이면 빈 목록으로 초기화
        if (joinRequestDTO.getGoal() == null) {
            joinRequestDTO.setGoal(new ArrayList<>());
        }
        
        memberService.join(joinRequestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "로그인, 인증처리 api")
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody
                                                  LoginRequestDTO loginRequestDTO,
                                                  HttpServletResponse response) {
        log.info("Login request: {}", loginRequestDTO);
        Map<String, Object> loginClaims = memberService.login(loginRequestDTO.getEmail(), loginRequestDTO.getPassword());

        String refreshToken = loginClaims.get("refreshToken").toString();
        String accessToken = loginClaims.get("accessToken").toString();

        CookieUtil.setTokenCookie(response, "refreshToken", refreshToken, jwtProps.getRefreshTokenExpirationPeriod());

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .name(loginClaims.get("name").toString())
                .userType(loginClaims.getOrDefault("userType", "MEMBER").toString())
                .id(((Number) loginClaims.get("id")).longValue())
                .accessToken(accessToken)
                .build();

        log.info("Login response: {}", loginResponseDTO);
        // 로그인 성공시, accessToken, email, name, userType 반환
        return ResponseEntity.ok(loginResponseDTO);
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        log.info("Logout");
        // accessToken은 react 내 redux 상태 지워서 없앰
        // 쿠키 삭제
        CookieUtil.removeTokenCookie(response, "refreshToken");

        return ResponseEntity.ok("logout success!");
    }


    @Operation(summary = "토큰 갱신", description = "refreshToken으로 새로운 accessToken을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class)) }),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 refreshToken"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(
            @Parameter(description = "refreshToken 쿠키", required = true)
            @CookieValue(value = "refreshToken") String refreshToken,
            HttpServletResponse response) {
        log.info("refresh refreshToken: {}", refreshToken);

        // ✳️ RefreshToken 검증해서 맴버정보 다시 가져옴!
        Map<String, Object> loginClaims = jwtUtil.validateToken(refreshToken);
        log.info("RefreshToken loginClaims: {}", loginClaims);

        String newAccessToken = jwtUtil.generateToken(loginClaims, jwtProps.getAccessTokenExpirationPeriod());
        String newRefreshToken = jwtUtil.generateToken(loginClaims, jwtProps.getRefreshTokenExpirationPeriod());

        // refreshToken 만료시간이 1시간 이하로 남았다면, 새로 발급
        if (checkTime((Integer) loginClaims.get("exp"))) {
            // 새로 발급
            CookieUtil.setTokenCookie(response, "refreshToken", newRefreshToken, jwtProps.getRefreshTokenExpirationPeriod()); // 1day
        } else {
            // 만료시간이 1시간 이상이면, 기존 refreshToken 그대로
            CookieUtil.setNewRefreshTokenCookie(response, "refreshToken", refreshToken);
        }

        LoginResponseDTO loginResponseDTO = LoginResponseDTO.builder()
                .email(loginClaims.get("email").toString())
                .name(loginClaims.get("name").toString())
                .userType(loginClaims.getOrDefault("userType", "MEMBER").toString())
                .id(((Number) loginClaims.get("id")).longValue())
                .accessToken(newAccessToken)
                .build();

        log.info("refresh loginResponseDTO: {}", loginResponseDTO);
        // refresh 성공시, accessToken, email, name, userType 반환
        return ResponseEntity.ok(loginResponseDTO);
    }


    // 회원가입시, 아이디(email) 중복확인 -> false, true 반환
    @GetMapping("/check-email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        log.info("checkEmail email: {}", email);
        return ResponseEntity.ok(memberService.checkEmail(email));
    }


    @GetMapping("/me")
    public MemberDetailDTO getMyInfo(@AuthenticationPrincipal MemberDTO member) {
        log.info("getMyInfo: {}", member);
        if(member == null) {
            return MemberDetailDTO.builder().build();
        }
        return memberService.getMyInfo(member.getEmail());
    }



}
