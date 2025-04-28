package org.iclass.wos.exception.advice;

import java.util.HashMap;
import java.util.Map;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
// 비즈니스 로직 예외처리
public class BusinessExceptionHandler {

    /**
     * 비즈니스 예외 처리 핸들러
     * @param e 비즈니스 예외
     * @return 클라이언트에게 반환할 오류 응답
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        log.error("비즈니스 오류: {} (코드: {}, 상태: {})", 
                 e.getMessage(), e.getErrorCode(), e.getHttpStatus().value());
        
        ApiResponse<Object> response = ApiResponse.error(e.getMessage(), e.getErrorCode());
        
        // 필요시 디버깅 정보 추가 (개발 환경에서만 사용)
        if (isDevEnvironment()) {
            Map<String, String> debugInfo = new HashMap<>();
            debugInfo.put("timestamp", String.valueOf(System.currentTimeMillis()));
            
            // 필요한 경우 스택 트레이스의 첫 몇 줄을 추가
            StackTraceElement[] stackTrace = e.getStackTrace();
            if (stackTrace.length > 0) {
                String errorLocation = stackTrace[0].getClassName() + "." 
                                    + stackTrace[0].getMethodName() 
                                    + "(" + stackTrace[0].getFileName() 
                                    + ":" + stackTrace[0].getLineNumber() + ")";
                debugInfo.put("errorLocation", errorLocation);
            }
            
            response = ApiResponse.error(e.getMessage(), e.getErrorCode(), debugInfo);
        }
        
        return new ResponseEntity<>(response, e.getHttpStatus());
    }
    
    /**
     * 현재 환경이 개발 환경인지 확인
     * @return 개발 환경 여부
     */
    private boolean isDevEnvironment() {
        // 프로파일 기반 또는 특정 환경 변수로 개발 환경 확인
        // 실제 구현에서는 Spring의 Environment를 주입받아 사용할 수 있습니다.
        String activeProfile = System.getProperty("spring.profiles.active");
        return activeProfile != null && 
              (activeProfile.equals("dev") || activeProfile.equals("local"));
    }
}