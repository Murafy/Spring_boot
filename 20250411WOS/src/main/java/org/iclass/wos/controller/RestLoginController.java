package org.iclass.wos.controller;

import java.util.HashMap;
import java.util.Map;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.LoginFailureDto;
import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.dto.UserAccountOutDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.security.jwt.JwtTokenProvider;
import org.iclass.wos.service.LoginSecurityService;
import org.iclass.wos.service.RefreshTokenService;
import org.iclass.wos.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class RestLoginController {
    @Autowired
    private UserAccountService userService;
    
    @Autowired
    private LoginSecurityService securityService;
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private RefreshTokenService refreshTokenService;
    
    @GetMapping("/refresh-token")
    public ResponseEntity<ApiResponse<Map<String, Object>>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // 쿠키에서 Refresh Token 추출
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        
        if (refreshToken == null) {
            throw new BusinessException("유효한 갱신 토큰이 없습니다.", ErrorCode.INVALID_TOKEN);
        }
        
        // Refresh Token 유효성 검증
        if (!refreshTokenService.validateRefreshToken(refreshToken)) {
            throw new BusinessException("갱신 토큰이 유효하지 않습니다. 다시 로그인해주세요.", ErrorCode.TOKEN_EXPIRED);
        }
        
        // 유효한 경우 사용자 ID 추출
        String userId = refreshTokenService.getUserIdFromRefreshToken(refreshToken);
        
        // 사용자 권한 조회
        String role = userService.findRoleById(userId);
        
        // 새 Access Token 생성
        String newAccessToken = tokenProvider.createAccessToken(userId, role);
        
        // 새 Refresh Token 생성 (Refresh Token Rotation 적용)
        String newRefreshToken = refreshTokenService.createRefreshToken(userId);
        
        // 쿠키에 토큰 저장
        ResponseCookie accessTokenCookie = tokenProvider.createAccessTokenCookie(newAccessToken);
        ResponseCookie refreshTokenCookie = tokenProvider.createRefreshTokenCookie(newRefreshToken);
        
        // 응답 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", newAccessToken); // 클라이언트에서 사용할 수 있도록 토큰 값도 함께 반환
        responseData.put("tokenType", "Bearer");
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success("토큰이 갱신되었습니다.", responseData));
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody UserAccountDto loginRequest, 
                                  HttpServletRequest request, 
                                  HttpServletResponse response) {
        String id = loginRequest.getId();
        
        // 계정 잠금 상태 확인
        if (securityService.isAccountLocked(id)) {
            throw new BusinessException(
                "계정이 잠금 상태입니다. " + securityService.getLockDurationMinutes() + "분 후에 다시 시도하세요.",
                ErrorCode.ACCOUNT_LOCKED
            );
        }

        UserAccountDto user = userService.login(loginRequest);
        
        if (user == null) {
            // 로그인 실패 활동 기록
            securityService.logLoginActivity(id, false, request);
            
            // 로그인 실패 처리 및 계정 잠금 여부 확인
            boolean isLocked = false;
            
            // 유효한 사용자 ID인 경우에만 실패 처리
            if (userService.findNicknameById(id) != null) {
                // 실패 처리 
                isLocked = securityService.handleLoginFailure(id);
                
                // 현재 실패 카운트 가져오기
                LoginFailureDto status = securityService.getAccountStatus(id);
                int failCount = status.getFailCount();
                int maxFailCount = securityService.getMaxFailCount();
                
                // 실패 처리 후 상태 로깅
                log.info("로그인 실패 처리 후 상태: 사용자 ID={}, 실패 횟수={}/{}, 잠금 상태={}", 
                        id, failCount, maxFailCount, isLocked);
                
                if (isLocked) {
                    throw new BusinessException(
                        "로그인 시도 횟수를 초과했습니다. 계정이 " + 
                        securityService.getLockDurationMinutes() + "분 동안 잠금 상태로 전환됩니다.",
                        ErrorCode.ACCOUNT_LOCKED
                    );
                } else {
                    // 남은 시도 횟수 표시
                    throw new BusinessException(
                        "아이디와 패스워드를 확인해주세요. 남은 시도 횟수: " + 
                        (maxFailCount - failCount) + "회",
                        ErrorCode.INVALID_CREDENTIALS
                    );
                }
            } else {
                // 존재하지 않는 사용자는 로그만 남김
                log.warn("존재하지 않는 사용자 ID로 로그인 시도: {}", id);
                
                throw new BusinessException(
                    "아이디와 패스워드를 확인해주세요.",
                    ErrorCode.INVALID_CREDENTIALS
                );
            }
        }
        
        // 로그인 성공: 닉네임과 권한 조회
        String nickname = userService.findNicknameById(id);
        String role = userService.findRoleById(id);
        
        // 로그인 성공 시 실패 카운트 초기화
        securityService.resetLoginFailCount(id);
        
        // 로그인 활동 기록
        securityService.logLoginActivity(id, true, request);
        
        // JWT 토큰 생성
        String accessToken = tokenProvider.createAccessToken(id, role);
        String refreshToken = refreshTokenService.createRefreshToken(id);
        
        // 쿠키 생성
        ResponseCookie accessTokenCookie = tokenProvider.createAccessTokenCookie(accessToken);
        ResponseCookie refreshTokenCookie = tokenProvider.createRefreshTokenCookie(refreshToken);
        
        // 응답 데이터 구성
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("accessToken", accessToken);
        responseData.put("tokenType", "Bearer");
        responseData.put("user", UserAccountOutDto.builder()
                .id(id)
                .nickname(nickname)
                .role(role)
                .profileImageUrl(userService.getProfileImageUrl(id))
                .build());
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(ApiResponse.success("로그인에 성공했습니다.", responseData));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkLoginStatus(HttpServletRequest request) {
        // 토큰에서 사용자 정보 추출
        String token = extractTokenFromRequest(request);
        boolean isLoggedIn = false;
        Map<String, Object> responseData = new HashMap<>();
        
        if (token != null && tokenProvider.validateToken(token)) {
            String userId = tokenProvider.getUserIdFromToken(token);
            if (userId != null) {
                isLoggedIn = true;
                
                // 사용자 정보 조회
                String nickname = userService.findNicknameById(userId);
                String role = userService.findRoleById(userId);
                String profileImageUrl = userService.getProfileImageUrl(userId);
                
                responseData.put("id", userId);
                responseData.put("nickname", nickname);
                responseData.put("role", role);
                responseData.put("profileImageUrl", profileImageUrl);
                responseData.put("isAdmin", "ADMIN".equals(role));
            }
        }
        
        responseData.put("isLoggedIn", isLoggedIn);
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }
    
    // 계정 잠금 상태 확인
    @GetMapping("/account-status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkAccountStatus(@RequestParam("id") String userId) {
        // 기본적인 잠금 상태 확인
        boolean isLocked = securityService.isAccountLocked(userId);
        
        // 더 자세한 계정 상태 정보 가져오기
        LoginFailureDto accountStatus = securityService.getAccountStatus(userId);
        
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("isLocked", isLocked);
        responseData.put("failCount", accountStatus.getFailCount());
        
        if (isLocked) {
            // 잠금 해제까지 남은 시간 계산은 서비스에서 처리하도록 수정 필요
            long remainingMinutes = securityService.getRemainingLockTime(userId);
            
            responseData.put("remainingMinutes", remainingMinutes);
            responseData.put("message", "계정이 잠금 상태입니다. " + remainingMinutes + "분 후에 자동으로 잠금이 해제됩니다.");
        }
        
        return ResponseEntity.ok(ApiResponse.success(responseData));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        // 액세스 토큰 쿠키 삭제
        ResponseCookie clearAccessTokenCookie = tokenProvider.createEmptyAccessTokenCookie();
        
        // 리프레시 토큰 쿠키 삭제
        ResponseCookie clearRefreshTokenCookie = tokenProvider.createEmptyRefreshTokenCookie();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearAccessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, clearRefreshTokenCookie.toString())
                .body(ApiResponse.success("로그아웃되었습니다."));
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