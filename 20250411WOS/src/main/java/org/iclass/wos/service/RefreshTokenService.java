package org.iclass.wos.service;

import java.time.LocalDateTime;

import org.iclass.wos.dto.RefreshTokenDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.RefreshTokenMapper;
import org.iclass.wos.security.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    
    private final RefreshTokenMapper refreshTokenMapper;
    private final JwtTokenProvider tokenProvider;
    
    /**
     * 새로운 Refresh Token 생성
     * @param userId 사용자 ID
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("사용자 ID가 유효하지 않습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        try {
            String refreshToken = tokenProvider.createRefreshToken(userId);
            
            // 현재 시간과 토큰 만료 시간 계산 (날짜를 LocalDateTime으로 변환)
            LocalDateTime expiryDate = LocalDateTime.now().plusSeconds(
                    tokenProvider.getRefreshTokenExpirationMs() / 1000);
            
            // DTO 생성 및 저장
            RefreshTokenDto refreshTokenDto = RefreshTokenDto.builder()
                    .userId(userId)
                    .token(refreshToken)
                    .expiryDate(expiryDate)
                    .build();
            
            int result = refreshTokenMapper.saveToken(refreshTokenDto);
            if (result <= 0) {
                throw new BusinessException("토큰 저장에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
            
            return refreshToken;
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("리프레시 토큰 생성 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("리프레시 토큰 생성 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * Refresh Token 유효성 검증
     * @param token 검증할 토큰
     * @return 유효 여부
     */
    public boolean validateRefreshToken(String token) {
        if (token == null) {
            return false;
        }
        
        try {
            // JWT 유효성 검증
            if (!tokenProvider.validateToken(token)) {
                return false;
            }
            
            // 데이터베이스에서 토큰 조회
            RefreshTokenDto refreshToken = refreshTokenMapper.findByToken(token);
            
            // 토큰이 DB에 존재하고 만료되지 않았는지 확인
            return refreshToken != null && 
                   !refreshToken.getExpiryDate().isBefore(LocalDateTime.now());
        } catch (Exception e) {
            log.error("토큰 검증 중 오류: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Refresh Token에서 사용자 ID 추출
     * @param token Refresh Token
     * @return 사용자 ID
     */
    public String getUserIdFromRefreshToken(String token) {
        if (token == null) {
            throw new BusinessException("토큰이 유효하지 않습니다.", ErrorCode.INVALID_TOKEN);
        }
        
        try {
            return tokenProvider.getUserIdFromToken(token);
        } catch (Exception e) {
            log.error("토큰에서 사용자 ID 추출 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("토큰에서 사용자 정보를 추출할 수 없습니다.", ErrorCode.INVALID_TOKEN);
        }
    }
    
    /**
     * 사용자의 모든 리프레시 토큰 삭제
     * @param userId 사용자 ID
     */
    public void deleteByUserId(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("사용자 ID가 유효하지 않습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        try {
            refreshTokenMapper.deleteByUserId(userId);
        } catch (Exception e) {
            log.error("사용자 토큰 삭제 중 오류: {}", e.getMessage(), e);
            throw new BusinessException("사용자 토큰 삭제 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}