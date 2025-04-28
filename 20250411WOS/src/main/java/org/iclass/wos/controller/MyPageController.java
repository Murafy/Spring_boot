package org.iclass.wos.controller;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.ProfileUpdateDto;
import org.iclass.wos.dto.UserAccountOutDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.MyPageService;
import org.iclass.wos.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final UserAccountService userAccountService;
    private final MyPageService myPageService;

    // View	
    @GetMapping("/mypage")
    public String myPage(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info("마이페이지 접속 - 사용자 ID: {}", userId);

        UserAccountOutDto userInfo = userAccountService.getUserDetails(userId);
        model.addAttribute("user", userInfo);
        return "mypage";
    }
    
    // 패스워드 변경
    @PostMapping("/api/mypage/change-password")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info("비밀번호 변경 요청 - 사용자 ID: {}", userId);

        try {
            boolean result = myPageService.changePassword(userId, currentPassword, newPassword);
            if (!result) {
                throw new BusinessException("현재 비밀번호가 일치하지 않습니다.", ErrorCode.INVALID_PASSWORD);
            }
        } catch (Exception e) {
            throw new BusinessException("비밀번호 변경 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

    // 프로필 사진
    @PostMapping("/api/mypage/upload-profile")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> uploadProfileImage(
            @RequestParam("file") MultipartFile file) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        log.info("프로필 이미지 업로드 요청 - 사용자 ID: {}", userId);

        try {
            String imageUrl = myPageService.uploadProfileImage(file, userId);
            return ResponseEntity.ok(
                ApiResponse.success("프로필 이미지가 성공적으로 업로드되었습니다.", imageUrl)
            );
        } catch (Exception e) {
            throw new BusinessException("프로필 이미지 업로드 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    
    // 프로필 업데이트
    @PostMapping("/api/mypage/update-profile")
    @ResponseBody
    public ResponseEntity<ApiResponse<String>> updateProfile(
            @Valid @RequestBody ProfileUpdateDto updateRequest) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        updateRequest.setId(userId);

        log.info("프로필 정보 업데이트 요청 - 사용자 ID: {}", userId);

        try {
            boolean result = myPageService.updateProfile(updateRequest);
            if (!result) {
                throw new BusinessException("프로필 정보 업데이트에 실패했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            throw new BusinessException("프로필 정보 업데이트 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
        }

        return ResponseEntity.ok(ApiResponse.success("프로필 정보가 성공적으로 업데이트되었습니다."));
    }
}