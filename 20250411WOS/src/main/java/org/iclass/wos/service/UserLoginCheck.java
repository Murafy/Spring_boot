package org.iclass.wos.service;

import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.dto.UserAccountOutDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.UserAccountMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor // lombok 으로 bean 주입
public class UserLoginCheck implements UserAccountService {

    private final UserAccountMapper mapper; 
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserAccountDto login(UserAccountDto loginRequest) {
        // 입력 유효성 검사
        if (loginRequest == null || loginRequest.getId() == null || loginRequest.getPassword() == null) {
            throw new BusinessException("로그인 정보가 올바르지 않습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 사용자 정보 조회
        UserAccountDto user = mapper.findById(loginRequest.getId());
        log.info("UserLoginCheck 서비스의 user 리턴값 : {}", user);
        
        // 사용자가 존재하지 않거나 비밀번호가 일치하지 않으면 null 반환
        if (user == null) {
            log.warn("존재하지 않는 사용자 ID: {}", loginRequest.getId());
            return null;
        }
        
        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            log.warn("비밀번호 불일치: 사용자 ID={}", loginRequest.getId());
            return null;
        }
        
        return user;
    }
    
    @Override
    public String findNicknameById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        String nickname = mapper.findNicknameById(id);
        if (nickname == null) {
            log.warn("사용자 ID에 해당하는 닉네임을 찾을 수 없음: {}", id);
        }
        return nickname;
    }
    
    @Override
    public String findRoleById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        String role = mapper.findRoleById(id);
        if (role == null) {
            log.warn("사용자 ID에 해당하는 권한을 찾을 수 없음: {}", id);
        }
        return role;
    }

    @Override
    public boolean isLoggedIn(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException("유효하지 않은 요청입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        HttpSession session = request.getSession(false); // false: 세션이 없으면 null 반환
        return session != null && session.getAttribute("username") != null; // 세션에 "username"이 있으면 로그인된 것으로 간주
    }

    @Override
    public UserAccountOutDto getUserDetails(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 사용자 기본 정보 조회
        UserAccountDto user = mapper.findById(userId);
        
        if (user == null) {
            log.warn("사용자를 찾을 수 없음: {}", userId);
            throw BusinessException.notFound("사용자", userId);
        }
        
        // UserAccountOutDto 객체 생성 및 반환
        return UserAccountOutDto.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .profileImageUrl(user.getProfile_image_url())
                .address(user.getAddress())
                .phone(user.getPhone())
                .join_date(user.getJoin_date())
                .role(user.getRole())
                .build();
    }
    
    // 사용자 프로필 URL 조회 
    @Override
    public String getProfileImageUrl(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("유효하지 않은 사용자 ID입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        UserAccountDto user = mapper.findById(userId);
        if (user == null) {
            log.warn("프로필 이미지 URL 조회 실패 - 사용자 없음: {}", userId);
            return null;
        }
        return user.getProfile_image_url();
    }
}