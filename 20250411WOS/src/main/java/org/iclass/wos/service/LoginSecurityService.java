package org.iclass.wos.service;

import java.util.List;

import org.iclass.wos.dto.LoginFailureDto;

import jakarta.servlet.http.HttpServletRequest;

public interface LoginSecurityService {
    // 로그인 활동 로그 기록
    void logLoginActivity(String userId, boolean isSuccess, HttpServletRequest request);
    
    // 로그인 실패 처리
    boolean handleLoginFailure(String userId);
    
    // 계정 잠금 상태 확인
    boolean isAccountLocked(String userId);
    
    // 계정 잠금 해제 (필요시 관리자나 시간 경과 후 자동 해제)
    void unlockAccount(String userId);
    
    // 계정 상태 조회
    LoginFailureDto getAccountStatus(String userId);
    
    // 로그인 성공 시 실패 카운트 초기화
    void resetLoginFailCount(String userId);
    
    // 잠금 시간(분) 조회
    int getLockDurationMinutes();
    
    // 최대 실패 허용 횟수 조회
    int getMaxFailCount();
    
    // 관리자 페이지 -> 잠긴 계정 목록 조회 
    List<LoginFailureDto> getLockedAccounts();
    
    // 계정 잠금 해제까지 남은 시간(분) 계산
    long getRemainingLockTime(String userId);
}