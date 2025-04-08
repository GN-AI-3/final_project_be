package com.example.final_project_be.security.filter;

import com.example.final_project_be.security.CustomUserDetailService;
import com.example.final_project_be.security.MemberDTO;
import com.example.final_project_be.util.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailService userDetailService;

    // 해당 필터로직(doFilterInternal)을 수행할지 여부를 결정하는 메서드
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri: " + path);

        // Pre-flight 요청은 필터를 타지 않도록 설정
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }
        // /api/member/로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/member/login") || path.startsWith("/api/member/join")
                || path.startsWith("/api/member/check-email")
                || path.startsWith("/api/member/refresh") || path.startsWith("/api/member/logout")
        ) {
            return true;
        }
        // /api/trainer/로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/trainer/login") || path.startsWith("/api/trainer/join")
                || path.startsWith("/api/trainer/check-email")
                || path.startsWith("/api/trainer/refresh") || path.startsWith("/api/trainer/logout")
        ) {
            return true;
        }
        // /view 이미지 불러오기 api로 시작하는 요청은 필터를 타지 않도록 설정
        if (path.startsWith("/api/image/")
        ) {
            return true;
        }

        // -----
        // health check
        if (path.startsWith("/health")) {
            return true;
        }
        // Swagger UI 경로 제외 설정
        if (path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            return true;
        }
        // h2-console 경로 제외 설정
        if (path.startsWith("/h2-console")) {
            return true;
        }

        // /favicon.ico 경로 제외 설정
        if (path.startsWith("/favicon.ico")) {
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("------------------JWTCheckFilter.................");
        log.info("request.getServletPath(): {}", request.getServletPath());
        log.info("..................................................");

        String autHeaderStr = request.getHeader("Authorization");
        log.info("autHeaderStr Authorization: {}", autHeaderStr);

        try {
            // Bearer accessToken 형태로 전달되므로 Bearer 제거
            String accessToken = autHeaderStr.substring(7);// Bearer 제거
            log.info("JWTCheckFilter accessToken: {}", accessToken);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            log.info("JWT claims: {}", claims);
            
            // claims에서 사용자 타입 확인 (기본값은 MEMBER)
            String userType = (String) claims.getOrDefault("userType", "MEMBER");
            String email = (String) claims.get("email");
            
            UserDetails userDetails;
            // URL 경로에 따라 권한 체크
            String path = request.getRequestURI();
            
            // trainer 경로에는 TRAINER만 접근 가능
            if (path.startsWith("/api/trainer") && !"TRAINER".equals(userType)) {
                throw new RuntimeException("트레이너 권한이 필요합니다.");
            }
            
            // member 경로에는 MEMBER만 접근 가능
            if (path.startsWith("/api/member") && !"MEMBER".equals(userType)) {
                throw new RuntimeException("회원 권한이 필요합니다.");
            }
            
            // 사용자 정보 로드
            userDetails = userDetailService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, userDetails.getPassword(), userDetails.getAuthorities());

            // SecurityContextHolder에 인증 객체 저장
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 다음 필터로 이동
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT Check Error...........");
            log.error("e.getMessage(): {}", e.getMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(Map.of("error", "ERROR_ACCESS_TOKEN"));

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}
