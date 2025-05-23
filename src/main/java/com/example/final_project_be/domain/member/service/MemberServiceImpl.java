package com.example.final_project_be.domain.member.service;

import com.example.final_project_be.domain.member.dto.JoinRequestDTO;
import com.example.final_project_be.domain.member.dto.MemberDetailDTO;
import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import com.example.final_project_be.props.JwtProps;
import com.example.final_project_be.security.CustomUserDetailService;
import com.example.final_project_be.security.MemberDTO;
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
public class MemberServiceImpl implements MemberService {

    private final JWTUtil jwtUtil;
    private final JwtProps jwtProps;
    private final CustomUserDetailService customUserDetailService;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomFileUtil fileUtil;


    @Override
    public void join(JoinRequestDTO request) {
        memberRepository.findByEmail(request.getEmail())
                .ifPresent(member -> {
                    throw new IllegalArgumentException("이미 존재하는 회원입니다!");
                });

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        Member member = Member.from(request);
        // 먼저 회원을 저장
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public Map<String, Object> login(String email, String password, String fcmToken) {
        UserDetails userDetails;
        try {
            // MEMBER 타입으로 사용자 로드 시도
            userDetails = customUserDetailService.loadUserByUsernameAndType(email, "MEMBER");
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("해당 이메일로 등록된 회원이 없습니다.");
        }
        
        MemberDTO memberAuthDTO = (MemberDTO) userDetails;
        log.info("email : {}, password : {}", email, password);

        if(!passwordEncoder.matches(password, memberAuthDTO.getPassword())) {
            throw new RuntimeException("비밀번호가 틀렸습니다.");
        }

        // FCM 토큰 업데이트
        if (fcmToken != null && !fcmToken.isBlank()) {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));
            
            log.info("Updating FCM token for member: {}, token: {}", email, fcmToken);
            member.updateFcmToken(fcmToken);
            memberRepository.save(member);
        }

        Map<String, Object> memberClaims = memberAuthDTO.getClaims();

        String accessToken = jwtUtil.generateToken(memberClaims, jwtProps.getAccessTokenExpirationPeriod());
        String refreshToken = jwtUtil.generateToken(memberClaims, jwtProps.getRefreshTokenExpirationPeriod());

        memberClaims.put("accessToken", accessToken);
        memberClaims.put("refreshToken", refreshToken);

        return memberClaims;
    }

    @Override
    public Member getEntity(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 회원이 없습니다. email: " + email));
    }

    @Transactional(readOnly = true)
    @Override
    public MemberDetailDTO getMyInfo(String email) {
        Member member = getEntity(email);
        return MemberDetailDTO.from(member);
    }

    @Override
    public Boolean checkEmail(String email) {
        return memberRepository.existsByEmail(email);
    }

    @Override
    public MemberDetailDTO getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return MemberDetailDTO.from(member);
    }
}
