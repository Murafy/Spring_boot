package org.iclass.wos.controller;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.UserRegisterDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.RegistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/regist")
@RequiredArgsConstructor
public class RestRegistController {
    private final RegistService regist;
	
	@PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody UserRegisterDto registerRequest) {
        // 비밀번호 확인 일치 검사 (DTO 유효성 검사에서 다루지 않은 부분)
        if (registerRequest.getConfirmPassword() != null && 
            !registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException("비밀번호와 비밀번호 확인이 일치하지 않습니다.", ErrorCode.INVALID_VALUE);
        }
        
        // 회원가입 처리
        boolean result = regist.register(registerRequest);
        log.info("회원가입 시도 결과: {}", result);
        
        if (result) {
            return ResponseEntity.ok(ApiResponse.success("회원가입이 완료되었습니다."));
        } else {
            throw new BusinessException("이미 존재하는 아이디 또는 닉네임입니다.", ErrorCode.DUPLICATE_USER);
        }
    }
}