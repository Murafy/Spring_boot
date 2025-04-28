package org.iclass.wos.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/email")
public class EmailVerificationController {
    
    private final EmailService emailService;
    private final Map<String, VerificationInfo> verificationCodes = new ConcurrentHashMap<>();
    

    public EmailVerificationController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @PostMapping("/send-verification")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(@Valid @RequestBody EmailVerificationRequest request) {
        String email = request.getEmail();
        log.info("이메일 인증 코드 발송 요청: {}", email);
        
        // 이메일 유효성 검사 
        if (!isValidEmail(email)) {
            throw new BusinessException("유효하지 않은 이메일 형식입니다.", ErrorCode.INVALID_VALUE);
        }
        
        // 랜덤 6자리 코드 생성
        String verificationCode = generateRandomCode();
        log.info("랜덤 6자리 코드 생성 확인: {}", verificationCode);
        
        // 이메일 발송
        emailService.sendVerificationEmail(email, verificationCode);
        log.info("이메일 발송 완료: {}", email);
        
        // 검증 코드 저장 (5분 유효)
        verificationCodes.put(email, new VerificationInfo(verificationCode, LocalDateTime.now().plusMinutes(5)));
        log.info("이메일 발송 코드 저장 확인: {}", verificationCodes);
        
        return ResponseEntity.ok(ApiResponse.success("인증번호가 발송되었습니다."));
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyCode(@Valid @RequestBody VerificationRequest request) {
        String email = request.getEmail();
        String code = request.getCode();
        log.info("이메일 인증 코드 확인 요청: {}, 코드: {}", email, code);
        
        // 이메일에 대한 인증 정보 확인
        VerificationInfo info = verificationCodes.get(email);
        
        if (info == null) {
            throw new BusinessException("인증 요청을 찾을 수 없습니다.", ErrorCode.RESOURCE_NOT_FOUND);
        }
        
        // 만료 확인
        if (LocalDateTime.now().isAfter(info.getExpiryTime())) {
            verificationCodes.remove(email);
            throw new BusinessException("인증 시간이 만료되었습니다.", ErrorCode.INVALID_TOKEN);
        }
        
        // 코드 일치 확인
        if (!info.getCode().equals(code)) {
            throw new BusinessException("인증번호가 일치하지 않습니다.", ErrorCode.INVALID_VALUE);
        }
        
        // 인증 성공 - 인증 완료 상태로 변경
        info.setVerified(true);
        
        return ResponseEntity.ok(ApiResponse.success("이메일 인증이 완료되었습니다."));
    }
    
    private String generateRandomCode() {
        return String.format("%06d", new Random().nextInt(999999));
    }
    
    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(regex, email);
    }
}

// DTO 클래스들
@Data
class EmailVerificationRequest {
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;
}

@Data
class VerificationRequest {
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;
    
    @NotBlank(message = "인증코드는 필수 입력 항목입니다.")
    private String code;
}

@Data
class VerificationInfo {
    private String code;
    private LocalDateTime expiryTime;
    private boolean verified;
    
    public VerificationInfo(String code, LocalDateTime expiryTime) {
        this.code = code;
        this.expiryTime = expiryTime;
        this.verified = false;
    }
}