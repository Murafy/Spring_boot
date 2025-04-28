package org.iclass.wos.controller;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.UserAccountOutDto;
import org.iclass.wos.security.jwt.JwtTokenProvider;
import org.iclass.wos.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final UserAccountService userService;

    /**
     * 단순 인증 상태 확인 API
     * 401 에러가 발생하지 않으면 인증된 것으로 간주
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAuthentication(HttpServletRequest request) {
        // Authorization 헤더에서 액세스 토큰 추출
        String token = extractTokenFromRequest(request);
        
        Map<String, Object> responseData = new HashMap<>();
        
        // 토큰이 없거나 유효하지 않으면 401 에러가 발생 (SecurityConfig에서 처리)
        if (token == null || !tokenProvider.validateToken(token)) {
            responseData.put("authenticated", false);
            return ResponseEntity.ok(ApiResponse.success(responseData));
        }
        
        // 토큰에서 사용자 ID 추출
        String userId = tokenProvider.getUserIdFromToken(token);
        UserAccountOutDto userDetails = userService.getUserDetails(userId);
        
        responseData.put("authenticated", true);
        responseData.put("user", userDetails);
        
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }
    
    /**
     * 요청에서 JWT 토큰 추출
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        // 먼저 Authorization 헤더에서 Bearer 토큰 확인
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
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