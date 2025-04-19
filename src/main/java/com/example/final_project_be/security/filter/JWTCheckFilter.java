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
    
    // AI 서버 관련 상수 정의
    private static final String AI_SERVER_HEADER = "X-API-KEY";

    // 해당 필터로직(doFilterInternal)을 수행할지 여부를 결정하는 메서드
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        log.info("check uri: " + path);

        // AI 서버에서 오는 요청 확인 (API 키 헤더가 있는 경우)
        String aiApiKey = request.getHeader(AI_SERVER_HEADER);
        if (aiApiKey != null && !aiApiKey.isEmpty()) {
            log.info("AI 서버에서 온 요청, JWT 필터 스킵: {}", path);
            return true;
        }

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

        // 채팅 엔드포인트 추가
        if (path.startsWith("/api/anonymous-chat/")) {
            return true;
        }

        // 테스트 API 추가
        if (path.startsWith("/api/v1/test/")) {
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

        // AI 서버 인증 헤더 체크
        String aiApiKey = request.getHeader(AI_SERVER_HEADER);
        if (aiApiKey != null && !aiApiKey.isEmpty()) {
            // AI 서버 요청이라면 바로 통과
            log.info("AI 서버 요청으로 판단하여 필터 통과");
            filterChain.doFilter(request, response);
            return;
        }

        String autHeaderStr = request.getHeader("Authorization");
        log.info("autHeaderStr Authorization: {}", autHeaderStr);
        
        // Authorization 헤더가 없는 경우 처리
        if (autHeaderStr == null || !autHeaderStr.startsWith("Bearer ")) {
            log.error("Authorization 헤더가 없거나 올바르지 않은 형식입니다.");
            
            ObjectMapper objectMapper = new ObjectMapper();
            String msg = objectMapper.writeValueAsString(Map.of("error", "MISSING_OR_INVALID_TOKEN"));
            
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
            return;
        }

        try {
            // Bearer accessToken 형태로 전달되므로 Bearer 제거
            String accessToken = autHeaderStr.substring(7);// Bearer 제거
            log.info("JWTCheckFilter accessToken: {}", accessToken);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);

            log.info("JWT claims: {}", claims);
            
            // claims에서 사용자 타입 확인 (기본값은 MEMBER)
            String userType = (String) claims.getOrDefault("userType", "MEMBER");
            String email = (String) claims.get("email");
            Long id = ((Number) claims.get("id")).longValue(); // id 추출
            
            log.info("JWT claims - userType: {}, email: {}, id: {}", userType, email, id);
            
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
