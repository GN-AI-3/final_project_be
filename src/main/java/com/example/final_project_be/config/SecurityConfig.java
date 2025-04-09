package com.example.final_project_be.config;

import com.example.final_project_be.security.filter.JWTCheckFilter;
import com.example.final_project_be.security.handler.CustomAccessDeniedHandler;
import com.example.final_project_be.security.handler.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity // Spring Security 설정을 활성화
@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity  // @PreAuthorize, @Secured, @RolesAllowed 어노테이션을 사용하기 위해 필요
public class SecurityConfig {

    private final JWTCheckFilter jwtCheckFilter;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("security config...............");

//        http
//                .authorizeHttpRequests((authorizedHttpRequests) -> authorizedHttpRequests
//                        .requestMatchers(new AntPathRequestMatcher("/**")).permitAll());

        http.authorizeHttpRequests(
                authorizeHttpRequests -> authorizeHttpRequests
                        // /api/admin/join, /api/admin/login,logout 모두 접근 가능
                        .requestMatchers(new AntPathRequestMatcher("/api/member/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/logout")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/refresh")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/member/check-email/**")).permitAll()
                        
                        // /api/trainer/ 공개 엔드포인트
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/join")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/login")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/logout")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/refresh")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/check-email/**")).permitAll()
                        
                        // MEMBER 권한 필요 엔드포인트
                        .requestMatchers(new AntPathRequestMatcher("/api/member/**")).hasRole("MEMBER")
                        
                        // TRAINER 권한 필요 엔드포인트
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/**")).hasRole("TRAINER")
                        
                        // 공통 엔드포인트
                        .requestMatchers(new AntPathRequestMatcher("/api/member/me")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/trainer/me")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/image/**")).permitAll()
                        // chat_message
                        .requestMatchers(new AntPathRequestMatcher("/api/chat/**")).permitAll()
                        // health check
                        .requestMatchers(new AntPathRequestMatcher("/health/**")).permitAll()
                        // 익명 채팅 엔드포인트 허용
                        .requestMatchers(new AntPathRequestMatcher("/api/anonymous-chat/**")).permitAll()
                        // 정적 리소스에 대한 접근 허용
                        .requestMatchers(new AntPathRequestMatcher("/favicon.ico")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-ui/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                        .anyRequest().authenticated()
        );



        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        http.sessionManagement(sessionConfig -> {
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        http.csrf(AbstractHttpConfigurer::disable);


        // h2-console, 해당 페이지가 동일한 출처에서만 프레임으로 로드될 수 있도록 설정
        http
                .headers(headers -> {
                    headers.addHeaderWriter(
                            new XFrameOptionsHeaderWriter(
                                    XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                            )
                    );

                });

        // JWT Check Filter 추가
        http.addFilterBefore(jwtCheckFilter,
                UsernamePasswordAuthenticationFilter.class);
        // exception authenticationEntryPoint 추가 401 에러 처리
        http.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(customAuthenticationEntryPoint);
        });
        // exceptionHandler, 접근 거부 핸들러 추가
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(customAccessDeniedHandler);
        });

        return http.build();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {



        CorsConfiguration configuration = new CorsConfiguration();

        // 출처 설정 (모든 출처 허용)
//        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // localhost:3000 -> 허용
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001"
        ));
        // 허용할 메서드 설정
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        // 허용할 헤더 설정
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // 자격 증명 허용 설정 (쿠키 등)
        configuration.setAllowCredentials(true);
        // content-disposition 허용 설정 -> excel 파일 다운로드시, 제목노출을 위해 필요!
        configuration.setExposedHeaders(Arrays.asList("Content-Disposition"));
        // CORS 설정을 특정 경로에 적용
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
