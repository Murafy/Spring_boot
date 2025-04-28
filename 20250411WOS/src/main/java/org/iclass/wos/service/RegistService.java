package org.iclass.wos.service;

import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.dto.UserRegisterDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.UserAccountMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RegistService {
    private final UserAccountMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder; // 분석

    /**
     * 회원가입 처리
     * 비밀번호 BCrypt 암호화
     */
    @Transactional
    public boolean register(UserRegisterDto userDto) {
    	log.info("회원가입 요청 정보: {}", userDto);
    	
        // 입력값 유효성 검사
        if (userDto.getId() == null || userDto.getId().trim().isEmpty()) {
            throw new BusinessException("아이디는 필수 입력 항목입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (userDto.getPassword() == null || userDto.getPassword().trim().isEmpty()) {
            throw new BusinessException("비밀번호는 필수 입력 항목입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (userDto.getNickname() == null || userDto.getNickname().trim().isEmpty()) {
            throw new BusinessException("닉네임은 필수 입력 항목입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 아이디 중복 확인
        if (userMapper.findById(userDto.getId()) != null) {
            log.warn("회원가입 실패: 이미 존재하는 아이디 - {}", userDto.getId());
            return false; // 이미 존재하는 아이디
        }

        // 닉네임 중복 확인
        if (userMapper.findByNickname(userDto.getNickname()) != null) {
            log.warn("회원가입 실패: 이미 존재하는 닉네임 - {}", userDto.getNickname());
            return false; // 이미 존재하는 닉네임
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        userDto.setPassword(encodedPassword);

        // 회원정보 저장
        int result = userMapper.insertUser(userDto);
        
        if (result <= 0) {
            throw new BusinessException("회원 정보 저장에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        log.info("회원가입 성공: {}", userDto.getId());
        return true;
    }
    
    /**
     * 로그인 처리
     * - BCrypt로 암호화된 비밀번호 검증
     */
    public UserAccountDto login(String id, String password) {
        // 유효성 검사
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("아이디는 필수 입력 항목입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (password == null || password.trim().isEmpty()) {
            throw new BusinessException("비밀번호는 필수 입력 항목입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 사용자 정보 조회
        UserAccountDto user = userMapper.findById(id);
        
        // 사용자가 존재하지 않거나 비밀번호가 일치하지 않으면 null 반환
        if (user == null) {
            throw new BusinessException("등록되지 않은 사용자입니다.", ErrorCode.USER_NOT_FOUND);
        }
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException("비밀번호가 일치하지 않습니다.", ErrorCode.INVALID_CREDENTIALS);
        }
        
        return user;
    }
}