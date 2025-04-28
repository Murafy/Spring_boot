package org.iclass.wos.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.iclass.wos.dto.ProfileUpdateDto;
import org.iclass.wos.dto.UserAccountDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.UserAccountMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService {

    private final UserAccountMapper userAccountMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    
    // 파일 저장 경로 (application.properties에서 설정)
    @Value("${file.upload.directory:uploads}")
    private String uploadDir;
    
    // 서버 URL (application.properties에서 설정)
    @Value("${server.url:http://localhost:8090}")
    private String serverUrl;

    @Override
    public String uploadProfileImage(MultipartFile file, String userId) {
        if (file.isEmpty()) {
            throw new BusinessException("파일이 비어있습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 사용자 존재 확인
        UserAccountDto user = userAccountMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND);
        }
        
        // 두단계로 분리해놓은 이유는 기본 업로드 디렉토리 (사진)이 존재한지 확인후 목적에 따른 폴더를 생성하기 위함임
        // properties 에 명시된 폴더 경로가 있는지 검사 후, 존재하지 않다면 해당 경로의 디렉토리 구조를 모두 생성함
        File uploadPathDir = new File(uploadDir);
        if (!uploadPathDir.exists()) {
            if (!uploadPathDir.mkdirs()) {
                throw new BusinessException("업로드 디렉토리를 생성할 수 없습니다.", ErrorCode.FILE_UPLOAD_ERROR);
            }
        }
        
        // 목적 폴더 생성 : 유저 프로필 이미지
        // 위 코드에서 검사를 하고 디렉토리 구조가 생성되면 해당 폴더 "하위" 폴더로 profiles 폴더 생성
        File profileDir = new File(uploadPathDir, "profiles");
        if (!profileDir.exists()) {
            if (!profileDir.mkdirs()) {
                throw new BusinessException("프로필 디렉토리를 생성할 수 없습니다.", ErrorCode.FILE_UPLOAD_ERROR);
            }
        }
        
        // 파일명 생성 (사용자 ID + UUID + 확장자)
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException("파일 이름을 가져올 수 없습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = userId + "_" + UUID.randomUUID().toString() + extension;
        
        // 파일 저장 경로
        File targetFile = new File(profileDir, newFilename);
        
        try {
            // 파일 저장
            file.transferTo(targetFile);
            
            // 이미지 URL 생성 (웹에서 접근 가능한 상대 경로)
            String imageUrl = "/uploads/profiles/" + newFilename;
            
            // DB에 이미지 URL 업데이트
            ProfileUpdateDto updateDto = new ProfileUpdateDto();
            updateDto.setId(userId);
            updateDto.setProfileImageUrl(imageUrl);
            updateProfile(updateDto);
            
            return imageUrl;
        } catch (IOException e) {
            log.error("파일 저장 중 오류 발생: {}", e.getMessage());
            throw new BusinessException("파일 저장 중 오류가 발생했습니다: " + e.getMessage(), ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
    
    @Override
    @Transactional
    public boolean updateProfile(ProfileUpdateDto updateRequest) {
        // 기존 사용자 정보 조회
        UserAccountDto user = userAccountMapper.findById(updateRequest.getId());
        
        if (user == null) {
            throw new BusinessException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND);
        }
        
        // 업데이트할 정보가 있는 필드만 설정
        if (updateRequest.getNickname() != null) {
            // 닉네임 중복 확인
            if (!user.getNickname().equals(updateRequest.getNickname())) {
                UserAccountDto existingUser = userAccountMapper.findByNickname(updateRequest.getNickname());
                if (existingUser != null) {
                    throw new BusinessException("이미 사용 중인 닉네임입니다.", ErrorCode.DUPLICATE_NICKNAME);
                }
                user.setNickname(updateRequest.getNickname());
            }
        }
        
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        
        if (updateRequest.getProfileImageUrl() != null) {
            user.setProfile_image_url(updateRequest.getProfileImageUrl());
        }
        
        if (updateRequest.getAddress() != null) {
            user.setAddress(updateRequest.getAddress());
        }
        
        if (updateRequest.getPhone() != null) {
            user.setPhone(updateRequest.getPhone());
        }
        
        // 사용자 정보 업데이트
        int result = userAccountMapper.updateUser(user);
        
        return result > 0;
    }
    
    @Override
    @Transactional
    public boolean changePassword(String userId, String currentPassword, String newPassword) {
        // 기존 사용자 정보 조회
        UserAccountDto user = userAccountMapper.findById(userId);
        
        if (user == null) {
            throw new BusinessException("사용자를 찾을 수 없습니다.", ErrorCode.USER_NOT_FOUND);
        }
        
        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false; // 현재 비밀번호 불일치
        }
        
        // 새 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);
        
        // 비밀번호 업데이트
        int result = userAccountMapper.updatePassword(userId, encodedPassword);
        
        if (result <= 0) {
            throw new BusinessException("비밀번호 변경에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
        
        return true;
    }
}