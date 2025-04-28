package org.iclass.wos.exception.advice;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.exception.ErrorCode;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler {

    /**
     * 일반적인 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("예상치 못한 오류 발생", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
            ErrorCode.INTERNAL_SERVER_ERROR.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 데이터베이스 접근 예외 처리
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException e) {
        log.error("데이터베이스 오류:", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "데이터베이스 처리 중 오류가 발생했습니다.",
            ErrorCode.DATABASE_ERROR.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    /**
     * 데이터 무결성 위반 예외 처리 (FK 제약 조건 위반 등)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("데이터 무결성 위반:", e);
        
        String message = "데이터 처리 중 오류가 발생했습니다.";
        // 중복키 위반 메시지 감지
        if (e.getMessage() != null && e.getMessage().contains("constraint")) {
            if (e.getMessage().contains("UNIQUE")) {
                message = "이미 존재하는 데이터입니다.";
            } else if (e.getMessage().contains("FOREIGN KEY")) {
                message = "연결된 데이터가 있어 처리할 수 없습니다.";
            }
        }
        
        ApiResponse<Object> response = ApiResponse.error(
            message,
            ErrorCode.DATA_INTEGRITY_ERROR.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    /**
     * 파일 업로드 최대 크기 초과 예외 처리
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSizeException(MaxUploadSizeExceededException e) {
        log.error("파일 크기 초과:", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "업로드 가능한 최대 파일 크기를 초과했습니다.",
            ErrorCode.FILE_SIZE_LIMIT_EXCEEDED.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.PAYLOAD_TOO_LARGE);
    }
    
    /**
     * 잘못된 인자 예외 처리
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("잘못된 인자 전달됨", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            e.getMessage(),
            ErrorCode.INVALID_REQUEST.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 메서드 인자 유효성 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("유효성 검증 실패", e);
        
        // 필드 오류 추출
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField, 
                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "유효하지 않은 값입니다.",
                    (existing, replacement) -> existing + ", " + replacement
                ));
        
        // 첫 번째 오류 메시지 추출
        String message = fieldErrors.isEmpty() ? 
                "유효하지 않은 요청 파라미터입니다." : 
                fieldErrors.get(0).getDefaultMessage();
        
        ApiResponse<Object> response = ApiResponse.error(
            message,
            ErrorCode.INVALID_VALUE.name(),
            errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 필수 파라미터 누락
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error("필수 파라미터 누락", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "필수 파라미터가 누락되었습니다: " + e.getParameterName(),
            ErrorCode.INVALID_REQUEST.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 인자 타입 불일치
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("인자 타입 불일치", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "요청 파라미터 타입이 올바르지 않습니다: " + e.getName(),
            ErrorCode.INVALID_REQUEST.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * JSON 파싱 오류
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("JSON 파싱 오류", e);
        
        // 메시지 상세화
        String message = "요청 본문을 파싱할 수 없습니다. 올바른 JSON 형식인지 확인하세요.";
        
        // JSON 파싱 오류 상세 정보 추출
        if (e.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) e.getCause();
            String fieldName = ife.getPath().stream()
                                .map(JsonMappingException.Reference::getFieldName)
                                .filter(field -> field != null)
                                .collect(Collectors.joining("."));
            
            if (!fieldName.isEmpty()) {
                Object value = ife.getValue();
                Class<?> targetType = ife.getTargetType();
                message = String.format("필드 '%s'의 값 '%s'는 타입 '%s'로 변환할 수 없습니다.", 
                                       fieldName, value, targetType.getSimpleName());
            }
        }
        
        ApiResponse<Object> response = ApiResponse.error(
            message,
            ErrorCode.INVALID_REQUEST.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * 바인딩 오류 (폼 제출 등)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("바인딩 오류", e);
        
        // 필드 오류 추출
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, String> errors = fieldErrors.stream()
                .collect(Collectors.toMap(
                    FieldError::getField, 
                    error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "유효하지 않은 값입니다.",
                    (existing, replacement) -> existing + ", " + replacement
                ));
        
        // 첫 번째 오류 메시지 추출
        String message = fieldErrors.isEmpty() ? 
                "요청 데이터 바인딩 오류가 발생했습니다." : 
                fieldErrors.get(0).getDefaultMessage();
        
        ApiResponse<Object> response = ApiResponse.error(
            message,
            ErrorCode.INVALID_REQUEST.name(),
            errors
        );
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    /**
     * I/O 예외 처리
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ApiResponse<Object>> handleIOException(IOException e) {
        log.error("I/O 오류", e);
        
        ApiResponse<Object> response = ApiResponse.error(
            "파일 처리 중 오류가 발생했습니다.",
            ErrorCode.FILE_UPLOAD_ERROR.name()
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}