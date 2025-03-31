package com.example.final_project_be.security;

import com.example.final_project_be.domain.member.entity.Member;
import com.example.final_project_be.domain.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("loadUserByUsername: username: {}", username);

        Member member = memberRepository.getWithRoles(username)
                .orElseThrow(() -> new UsernameNotFoundException("미존재하는 사용자 email: " + username));

        MemberDTO memberDTO = new MemberDTO(
                member.getEmail(),
                member.getPassword(),
                member.getPhone(),
                member.getName(),
                member.getMemberRoleList().stream().map(Enum::name).toList(),
                member.getMemberGoalList().stream().map(Enum::name).toList()
        );


        log.info("loadUserByUsername result memberDTO: {}", memberDTO);

        return memberDTO;
    }
}
