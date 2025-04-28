package org.iclass.wos.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iclass.wos.dto.LoginActivityDto;
import org.iclass.wos.dto.LoginFailureDto;

//Mappler -> LoginSecurity.xml
@Mapper
public interface LoginSecurityMapper {
    // 로그인 활동 로깅
    void insertLoginActivity(LoginActivityDto activity);
    
    // 로그인 실패 조회
    LoginFailureDto getLoginFailure(String userId);
    
    // 로그인 실패 추가/업데이트
    void insertLoginFailure(@Param("userId") String userId);
    void updateLoginFailure(LoginFailureDto failureDto);
    
    // 계정 잠금/잠금해제
    void lockAccount(@Param("userId") String userId);
    void unlockAccount(@Param("userId") String userId);
    
    // 관리자 페이지 -> 잠긴 계정 목록 조회 
    List<LoginFailureDto> getLockedAccounts();
}