package org.iclass.wos.controller;

import java.util.List;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.LoginFailureDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.LoginSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @Autowired
    private LoginSecurityService securityService;
    
    /**
     * 잠긴 계정 목록 조회 API
     * 관리자 권한 검사 없이 모든 사용자가 접근 가능하도록 수정
     */
    @GetMapping("/locked-accounts")
    public ResponseEntity<ApiResponse<List<LoginFailureDto>>> getLockedAccounts(HttpServletRequest request) {
        // 서비스를 통해 잠긴 계정 목록 조회
        List<LoginFailureDto> lockedAccounts = securityService.getLockedAccounts();
        
        log.info("잠긴 계정 목록 조회: 총 {}개 계정", lockedAccounts.size());
        return ResponseEntity.ok(ApiResponse.success(lockedAccounts));
    }
    
    @GetMapping("/get-locked-accounts-direct")
    public ResponseEntity<ApiResponse<List<LoginFailureDto>>> getLockedAccountsDirect() {
        List<LoginFailureDto> lockedAccounts = securityService.getLockedAccounts();
        return ResponseEntity.ok(ApiResponse.success(lockedAccounts));
    }
    
    @PostMapping("/unlock-account")
    public ResponseEntity<ApiResponse<String>> unlockAccount(
            @RequestParam("userId") String userId, 
            @RequestParam(value = "bypass", required = false) Boolean bypass,
            HttpServletRequest request) {
        
        // 계정 잠금 해제
        securityService.unlockAccount(userId);
        
        // 현재 상태 확인 (해제 확인)
        LoginFailureDto status = securityService.getAccountStatus(userId);
        boolean stillLocked = status.isLocked();
        
        if (stillLocked) {
            log.warn("계정 잠금 해제 실패: 사용자 ID={}", userId);
            throw new BusinessException("계정 잠금 해제에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        // 세션이 있는 경우 로깅, 없으면 대체 메시지
        HttpSession session = request.getSession(false);
        String loginUser = session != null ? (String)session.getAttribute("userid") : "anonymous";
        
        log.info("계정 잠금 해제 성공: 사용자 ID={}, 요청자={}", userId, loginUser);
        return ResponseEntity.ok(ApiResponse.success("계정 잠금이 해제되었습니다."));
    }
    
    @PostMapping("/unlock-account-direct")
    public ResponseEntity<ApiResponse<String>> unlockAccountDirect(@RequestParam("userId") String userId) {
        securityService.unlockAccount(userId);
        return ResponseEntity.ok(ApiResponse.success("계정 잠금이 해제되었습니다."));
    }
    
}