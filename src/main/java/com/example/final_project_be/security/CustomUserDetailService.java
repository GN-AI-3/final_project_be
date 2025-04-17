package com.example.final_project_be.security;

import com.example.final_project_be.domain.member.repository.MemberRepository;
import com.example.final_project_be.domain.trainer.repository.TrainerRepository;
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
    private final TrainerRepository trainerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername: username: {}", username);

        return memberRepository.findByEmail(username)
                .map(member -> {
                    MemberDTO memberDTO = new MemberDTO(
                            member.getId(),
                            member.getEmail(),
                            member.getPassword(),
                            member.getPhone(),
                            member.getName(),
                            member.getUserType(),
                            member.getMemberGoalList().stream().map(Enum::name).toList()
                    );
                    log.info("loadUserByUsername found member: {}", memberDTO);
                    return (UserDetails) memberDTO;
                })
                .orElseGet(() -> {
                    // Member에서 찾지 못한 경우 Trainer 테이블에서 검색
                    return trainerRepository.findByEmail(username)
                            .map(trainer -> {
                                TrainerDTO trainerDTO = new TrainerDTO(
                                        trainer.getId(),
                                        trainer.getEmail(),
                                        trainer.getPassword(),
                                        trainer.getPhone(),
                                        trainer.getName(),
                                        trainer.getUserType(),
                                        trainer.getCareer(),
                                        trainer.getCertifications(),
                                        trainer.getSpecialities()
                                );
                                log.info("loadUserByUsername found trainer: {}", trainerDTO);
                                return (UserDetails) trainerDTO;
                            })
                            .orElseThrow(() -> new UsernameNotFoundException("미존재하는 사용자 email: " + username));
                });
    }
    
    /**
     * 특정 유형의 사용자만 조회
     */
    public UserDetails loadUserByUsernameAndType(String username, String userType) {
        UserDetails userDetails = loadUserByUsername(username);
        
        if (userDetails instanceof MemberDTO) {
            MemberDTO memberDTO = (MemberDTO) userDetails;
            if (memberDTO.getUserType().equals(userType)) {
                return userDetails;
            }
        } else if (userDetails instanceof TrainerDTO) {
            TrainerDTO trainerDTO = (TrainerDTO) userDetails;
            if (trainerDTO.getUserType().equals(userType)) {
                return userDetails;
            }
        }
        
        throw new UsernameNotFoundException(userType + " 유형의 사용자가 아닙니다: " + username);
    }
}
