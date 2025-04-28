package org.iclass.wos.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.iclass.wos.dto.LoginActivityDto;
import org.iclass.wos.dto.LoginFailureDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.LoginSecurityMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class LoginSecurityServiceImpl implements LoginSecurityService {

    private final LoginSecurityMapper securityMapper;
    private static final int MAX_FAIL_COUNT = 5; // 최대 로그인 실패 횟수
    private static final int LOCK_MINUTES = 30; // 계정 잠금 시간(분)
    
    @Override
    @Transactional
    public void logLoginActivity(String userId, boolean isSuccess, HttpServletRequest request) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // 로그인 활동 객체 생성
        LoginActivityDto activity = LoginActivityDto.builder()
                .userId(userId)
                .loginTime(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .loginStatus(isSuccess ? "SUCCESS" : "FAILURE")
                .loginType("NORMAL")
                .build();
        
        // 로그 기록
        securityMapper.insertLoginActivity(activity);
        log.info("로그인 활동 기록: {}", activity);
        
        // 중요: 로그인 성공 시 실패 카운트 초기화
        if (isSuccess) {
            // 로그인 실패 기록 조회
            LoginFailureDto failure = securityMapper.getLoginFailure(userId);
            // 실패 기록이 있으면 초기화
            if (failure != null) {
                log.info("로그인 성공으로 실패 카운트 초기화: 사용자 ID={}, 이전 실패 횟수={}", 
                        userId, failure.getFailCount());
                unlockAccount(userId); // 계정 잠금 해제 및 실패 카운트 초기화
            }
        }
    }
    
    /**
     * 로그인 성공 시 실패 카운트를 초기화
     */
    @Transactional
    public void resetLoginFailCount(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        LoginFailureDto failure = securityMapper.getLoginFailure(userId);
        if (failure != null) {
            // 로그인 실패 기록이 있으면 실패 카운트 초기화
            securityMapper.unlockAccount(userId);
        }
    }
    
    @Override
    @Transactional
    public boolean handleLoginFailure(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 로그인 실패 기록 가져오기
        LoginFailureDto failure = securityMapper.getLoginFailure(userId);
        
        if (failure == null) {
            // 처음 실패하는 경우 새 기록 생성
            securityMapper.insertLoginFailure(userId);
            log.info("첫 로그인 실패 기록 생성: 사용자 ID={}, 실패 횟수=1", userId);
            return false; // 아직 잠금 상태 아님
        } else {
            // 기존 실패 기록 있는 경우
            
            // 이미 잠금 상태인지 확인
            if (failure.isLocked()) {
                // 잠금 시간이 지났는지 확인
                if (failure.getLockTime() != null && 
                    ChronoUnit.MINUTES.between(failure.getLockTime(), LocalDateTime.now()) >= LOCK_MINUTES) {
                    // 잠금 시간이 지났으면 잠금 해제
                    unlockAccount(userId);
                    
                    // 새로운 실패 기록 추가 (잠금 해제 후 첫 실패)
                    failure.setFailCount(1);
                    failure.setLastFailTime(LocalDateTime.now());
                    securityMapper.updateLoginFailure(failure);
                    log.info("잠금 해제 후 첫 실패: 사용자 ID={}, 실패 횟수=1", userId);
                    return false;
                }
                return true; // 여전히 잠금 상태
            }
            
            // 실패 횟수 증가
            failure.setFailCount(failure.getFailCount() + 1);
            failure.setLastFailTime(LocalDateTime.now());
            securityMapper.updateLoginFailure(failure);
            log.info("로그인 실패 횟수 증가: 사용자 ID={}, 실패 횟수={}/{}", 
                    userId, failure.getFailCount(), MAX_FAIL_COUNT);
            
            // 중요: 확실히 실패 횟수가 임계값 이상인지 확인
            if (failure.getFailCount() >= MAX_FAIL_COUNT) {
                securityMapper.lockAccount(userId);
                log.warn("계정 잠금 처리: 사용자 ID={}, 실패 횟수={}", userId, failure.getFailCount());
                return true; // 계정 잠김
            }
            
            return false; // 아직 잠금 상태 아님
        }
    }
    
    @Override
    public boolean isAccountLocked(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        LoginFailureDto failure = securityMapper.getLoginFailure(userId);
        if (failure == null) {
            return false;
        }
        
        if (failure.isLocked()) {
            // 잠금 시간이 지났는지 확인
            if (failure.getLockTime() != null && 
                ChronoUnit.MINUTES.between(failure.getLockTime(), LocalDateTime.now()) >= LOCK_MINUTES) {
                // 잠금 시간이 지났으면 잠금 해제
                unlockAccount(userId);
                return false;
            }
            return true; // 아직 잠금 상태
        }
        
        return false;
    }
    
    @Override
    @Transactional
    public void unlockAccount(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        securityMapper.unlockAccount(userId);
        log.info("계정 잠금 해제 및 실패 카운트 초기화: 사용자 ID={}", userId);
    }
    
    @Override
    public int getLockDurationMinutes() {
        return LOCK_MINUTES;
    }
    
    @Override
    public int getMaxFailCount() {
        return MAX_FAIL_COUNT;
    }
    
    @Override
    public LoginFailureDto getAccountStatus(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        LoginFailureDto failure = securityMapper.getLoginFailure(userId);
        
        // 계정 정보가 없으면 새로운 객체 생성하여 반환
        if (failure == null) {
            return LoginFailureDto.builder()
                .userId(userId)
                .failCount(0)
                .isLocked(false)
                .build();
        }
        
        // 잠금 상태이지만 잠금 시간이 지난 경우 자동으로 해제
        // 시간차이를 분(Min) 단위로 계산하는 메서드 사용 (첫번째 인자로부터 두번째 인자까지 지난 시간을 계산한다.)
        if (failure.isLocked() && 
            failure.getLockTime() != null && 
            ChronoUnit.MINUTES.between(failure.getLockTime(), LocalDateTime.now()) >= LOCK_MINUTES) {
            // 잠금시간부터 현재까지 경과된 시간이 30분 이상이라면 잠금해제
            unlockAccount(userId); 
            // 갱신된 상태 조회
            failure = securityMapper.getLoginFailure(userId);
        }
        
        return failure;
    }
    
    /**
     * 계정 잠금 해제까지 남은 시간(분)을 계산
     */
    @Override
    public long getRemainingLockTime(String userId) {
        LoginFailureDto failure = securityMapper.getLoginFailure(userId);
        
        if (failure == null || !failure.isLocked() || failure.getLockTime() == null) {
            return 0;
        }
        
        long elapsedMinutes = ChronoUnit.MINUTES.between(failure.getLockTime(), LocalDateTime.now());
        return Math.max(0, LOCK_MINUTES - elapsedMinutes);
    }
    
    // 클라이언트 IP 주소 가져오기
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }
    
    // 관리자 페이지 -> 잠긴 계정 목록 조회
    @Override
    public List<LoginFailureDto> getLockedAccounts() {
        List<LoginFailureDto> lockedAccounts = securityMapper.getLockedAccounts();
        
        // 잠금 시간이 경과한 계정 필터링 또는 자동 해제
        return lockedAccounts.stream()
                .filter(account -> {
                    // 잠금 시간이 경과했는지 확인
                    if (account.getLockTime() != null && 
                        ChronoUnit.MINUTES.between(account.getLockTime(), LocalDateTime.now()) >= LOCK_MINUTES) {
                        // 자동으로 잠금 해제
                        unlockAccount(account.getUserId());
                        return false; // 목록에서 제외
                    }
                    return true; // 여전히 잠금 상태인 계정만 반환
                })
                .collect(Collectors.toList());
    }
}