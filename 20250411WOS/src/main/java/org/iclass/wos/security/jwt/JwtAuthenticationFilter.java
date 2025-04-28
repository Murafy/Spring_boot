package org.iclass.wos.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    
    public JwtAuthenticationFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    // @NonNull: 해당 파라미터는 null이 될 수 없음을 명시함.
    // HttpServletRequest 등은 서블릿 스펙상 절대 null이 아님.
    // IDE 경고 제거 + 명확한 의도 표현을 위해 @NonNull 선언함.
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        log.info("요청 uri 확인 : {}", uri);
        
        // 루트 경로 리다이렉트 처리
        if (uri.equals("/")) {
            response.sendRedirect("/main");
            return;
        }
        
        // JWT 토큰 추출
        String jwt = extractTokenFromRequest(request);
        
        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("JWT 인증 성공: {}", authentication.getName());
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    // 토큰 추출 메서드
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 먼저 Authorization 헤더에서 Bearer 토큰 확인
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        
        // Authorization 헤더에 없으면 쿠키에서 확인
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}