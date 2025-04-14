package com.example.final_project_be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of(0L); // 시스템 사용자 ID
            }

            // 인증된 사용자의 ID를 반환
            Object principal = authentication.getPrincipal();
            if (principal instanceof com.example.final_project_be.security.TrainerDTO) {
                return Optional.of(((com.example.final_project_be.security.TrainerDTO) principal).getId());
            } else if (principal instanceof com.example.final_project_be.security.MemberDTO) {
                return Optional.of(((com.example.final_project_be.security.MemberDTO) principal).getId());
            }

            // 기본적으로 0L 반환 (시스템 사용자)
            return Optional.of(0L);
        };
    }
}
