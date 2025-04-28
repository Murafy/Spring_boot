package org.iclass.wos.exception.advice;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class AuthenticationExceptionHandler {

    /**
     * 인증 실패 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException e) {
        log.error("인증 실패: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "인증에 실패했습니다. 로그인이 필요합니다.",
            ErrorCode.UNAUTHORIZED.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 잘못된 인증 정보 예외 처리
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        log.error("잘못된 인증 정보: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "아이디 또는 비밀번호가 일치하지 않습니다.",
            ErrorCode.INVALID_CREDENTIALS.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 계정 잠금 예외 처리
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleLockedException(LockedException e) {
        log.error("계정 잠금: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "계정이 잠겨 있습니다. 관리자에게 문의하거나 잠시 후 다시 시도해주세요.",
            ErrorCode.ACCOUNT_LOCKED.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 계정 비활성화 예외 처리
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> handleDisabledException(DisabledException e) {
        log.error("계정 비활성화: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "계정이 비활성화되었습니다. 관리자에게 문의해주세요.",
            "ACCOUNT_DISABLED"
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 계정 만료 예외 처리
     */
    @ExceptionHandler(AccountExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountExpiredException(AccountExpiredException e) {
        log.error("계정 만료: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "계정이 만료되었습니다. 관리자에게 문의해주세요.",
            "ACCOUNT_EXPIRED"
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 자격 증명 만료 예외 처리
     */
    @ExceptionHandler(CredentialsExpiredException.class)
    public ResponseEntity<ApiResponse<Object>> handleCredentialsExpiredException(CredentialsExpiredException e) {
        log.error("자격 증명 만료: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "비밀번호가 만료되었습니다. 비밀번호를 변경해주세요.",
            "CREDENTIALS_EXPIRED"
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 사용자를 찾을 수 없는 예외 처리
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("사용자를 찾을 수 없음: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "아이디 또는 비밀번호가 일치하지 않습니다.",
            ErrorCode.USER_NOT_FOUND.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 인증이 부족한 경우 예외 처리
     */
    @ExceptionHandler(InsufficientAuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleInsufficientAuthenticationException(InsufficientAuthenticationException e) {
        log.error("인증 부족: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "접근하려면 로그인이 필요합니다.",
            "AUTH_REQUIRED"
        );
        
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    /**
     * 접근 거부 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException e) {
        log.error("접근 거부: {}", e.getMessage());
        
        ApiResponse<Object> response = ApiResponse.error(
            "해당 기능에 접근할 권한이 없습니다.",
            ErrorCode.ACCESS_DENIED.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
}