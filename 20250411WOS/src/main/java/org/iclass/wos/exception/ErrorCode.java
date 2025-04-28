package org.iclass.wos.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

/**
 * 애플리케이션 전반에서 사용하는 표준화된 오류 코드 정의
 */
@Getter
public enum ErrorCode {
    // 공통 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "유효하지 않은 요청입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스를 찾을 수 없습니다."),
    
    // 사용자 관련 오류
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 일치하지 않습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "계정이 잠금 상태입니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이미 존재하는 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다."),
    
    // 인증 관련 오류
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    
    // 엔티티 관련 오류
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "엔티티를 찾을 수 없습니다."),
    DUPLICATE_ENTITY(HttpStatus.CONFLICT, "중복된 엔티티가 존재합니다."),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    
    // 게시글 관련 오류
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."),
    INVALID_LANGUAGE(HttpStatus.BAD_REQUEST, "지원하지 않는 언어입니다."),
    
    // 파일 관련 오류
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 중 오류가 발생했습니다."),
    FILE_SIZE_LIMIT_EXCEEDED(HttpStatus.PAYLOAD_TOO_LARGE, "업로드 가능한 최대 파일 크기를 초과했습니다."),
    
    // 데이터베이스 오류
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 처리 중 오류가 발생했습니다."),
    DATA_INTEGRITY_ERROR(HttpStatus.CONFLICT, "데이터 무결성 제약 조건을 위반했습니다.");
    
    private final HttpStatus httpStatus;
    private final String message;
    
    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}