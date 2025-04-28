package org.iclass.wos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.dto.UserRegisterDto;

@Mapper
public interface UserAccountMapper {
	// 로그인
	UserAccountDto login (UserAccountDto user);
	
	// 사용자 권한 조회
	String findRoleById(String id);
	
	// 로그인 성공시 닉네임 가져오기
	String findNicknameById(String id);
	
	 /**
     * 아이디로 사용자 찾기
     */
    UserAccountDto findById(String id);
    
    /**
     * 닉네임으로 사용자 찾기
     */
    UserAccountDto findByNickname(String nickname);
    
    /**
     * 사용자 등록
     */
    int insertUser(UserRegisterDto user);
    
    /**
     * 비밀번호 업데이트
     */
    int updatePassword(@Param("id") String id, @Param("password") String encodedPassword);
    
    /**
     * 사용자 정보 업데이트
     */
    int updateUser(UserAccountDto user);
    
    /**
     * 프로필 이미지 URL 업데이트
     */
    int updateProfileImage(@Param("id") String id, @Param("profileImageUrl") String profileImageUrl);
}