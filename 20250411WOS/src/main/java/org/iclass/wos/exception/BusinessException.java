package org.iclass.wos.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
// 비즈니스 로직 오류 처리 사용자 정의 예외 클래스
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    // 기존 방식 유지 (하위 호환성)
    private String errorCode;
    private HttpStatus httpStatus;
    
    // ErrorCode enum 추가
    private ErrorCode errorCodeEnum;
    
    /**
     * ErrorCode enum을 사용하는 생성자
     */
    public BusinessException(String message, ErrorCode errorCodeEnum) {
        super(message);
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.name();
        this.httpStatus = errorCodeEnum.getHttpStatus();
    }
    
    /**
     * ErrorCode enum만 사용하는 생성자
     */
    public BusinessException(ErrorCode errorCodeEnum) {
        super(errorCodeEnum.getMessage());
        this.errorCodeEnum = errorCodeEnum;
        this.errorCode = errorCodeEnum.name();
        this.httpStatus = errorCodeEnum.getHttpStatus();
    }
    
    /**
     * 기존 방식의 생성자 (String errorCode, HttpStatus)
     */
    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
        
        // 가능하면 ErrorCode enum에서 매핑 시도
        try {
            this.errorCodeEnum = ErrorCode.valueOf(errorCode);
        } catch (IllegalArgumentException e) {
            // 일치하는 enum 값이 없는 경우 무시
        }
    }
    
    /**
     * 기본 HTTP 상태 코드(400)를 사용하는 비즈니스 예외 생성자
     */
    public BusinessException(String message, String errorCode) {
        this(message, errorCode, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 지정된 HTTP 상태 코드로 비즈니스 예외 생성
     */
    public static BusinessException of(String message, HttpStatus httpStatus) {
        return new BusinessException(message, "BIZ_" + httpStatus.value(), httpStatus);
    }
    
    /**
     * 엔티티를 찾을 수 없는 경우 사용할 예외 생성
     */
    public static BusinessException notFound(String entityName, Object id) {
        return new BusinessException(
            String.format("%s(id: %s)를 찾을 수 없습니다.", entityName, id),
            ErrorCode.ENTITY_NOT_FOUND
        );
    }
    
    /**
     * 중복 데이터가 발견된 경우 사용할 예외 생성
     */
    public static BusinessException duplicate(String entityName, String fieldName, Object value) {
        return new BusinessException(
            String.format("%s의 %s '%s'는 이미 사용 중입니다.", entityName, fieldName, value),
            ErrorCode.DUPLICATE_ENTITY
        );
    }
    
    /**
     * 유효하지 않은 입력값인 경우 사용할 예외 생성
     */
    public static BusinessException invalidValue(String fieldName, String message) {
        return new BusinessException(
            String.format("%s: %s", fieldName, message),
            ErrorCode.INVALID_VALUE
        );
    }
}