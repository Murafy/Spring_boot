package org.iclass.wos.service;

import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.dto.UserAccountOutDto;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

// 해당 인터페이스의 구현체는 UserLoginCheck임 
@Service
public interface UserAccountService {
	// 로그인 
    UserAccountDto login(UserAccountDto loginRequest); 
    
    // 사용자 권한 조회
    String findRoleById(String id);
    
    // 로그인 성공시 DB에서 닉네임 끌어오기
    String findNicknameById(String id);
    
    // 로그인 여부 
    boolean isLoggedIn(HttpServletRequest request);
    
    // 사용자 상세 정보 조회
    UserAccountOutDto getUserDetails(String userId);
    
    // 사용자 프로필 URL 조회
    String getProfileImageUrl(String userId);
}