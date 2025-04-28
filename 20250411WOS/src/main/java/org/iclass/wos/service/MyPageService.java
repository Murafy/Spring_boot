package org.iclass.wos.service;

import org.iclass.wos.dto.ProfileUpdateDto;
import org.springframework.web.multipart.MultipartFile;

public interface MyPageService {
    
    /**
     * 프로필 이미지를 업로드하고 URL을 반환
     */
    String uploadProfileImage(MultipartFile file, String userId) throws Exception;
    
    /**
     * 프로필 정보 업데이트
     */
    boolean updateProfile(ProfileUpdateDto updateRequest) throws Exception;
    
    /**
     * 비밀번호 변경
     */
    boolean changePassword(String userId, String currentPassword, String newPassword) throws Exception;
}