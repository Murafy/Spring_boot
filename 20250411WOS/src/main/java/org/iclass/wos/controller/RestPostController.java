package org.iclass.wos.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.PageResponseDTO;
import org.iclass.wos.dto.PostDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
public class RestPostController {
	private final PostService service;

	// 언어별 게시글 목록 조회 API 엔드포인트
	@GetMapping("api/board/{language}")
	public CompletableFuture<ResponseEntity<ApiResponse<PageResponseDTO>>> getBoardListByLanguage(
			@PathVariable("language") String language,
			@RequestParam(defaultValue = "1") int page) {

		return service.getPageListAsync(language, page)
				.thenApply(pageResponseDTO -> {
					log.info("정상 응답시 각 게시판 데이터 : {}", pageResponseDTO);
					return ResponseEntity.ok(ApiResponse.success(pageResponseDTO));
				})
				.exceptionally(e -> {
					if (e.getCause() instanceof BusinessException) {
						throw (BusinessException) e.getCause();
					}
					throw new BusinessException("게시글 목록을 가져오는 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
				});
	}
	
	@GetMapping("api/read")
	public CompletableFuture<ResponseEntity<ApiResponse<PostDto>>> getPostDetails(
			@RequestParam("language") String language,
			@RequestParam("idx") int idx) {
		return service.read(language, idx)
				.thenApply(postDto -> ResponseEntity.ok(ApiResponse.success(postDto)))
				.exceptionally(e -> {
					if (e.getCause() instanceof BusinessException) {
						throw (BusinessException) e.getCause();
					}
					throw new BusinessException("게시글을 조회하는 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
				});
	}
	
	// 새 게시글 저장 API 엔드포인트
    @PostMapping("api/post/save")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> savePost(
            @RequestBody PostDto postDto,
            HttpServletRequest request) {
        
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("로그인이 필요합니다.", ErrorCode.UNAUTHORIZED);
        }
        
        String writer = authentication.getName();
        // 세션에서 닉네임 정보 가져오기 (필요한 경우)
        String nickname = (String) request.getSession(false).getAttribute("nickname");
        postDto.setWriter(nickname != null ? nickname : writer);
        
        // IP 정보 설정
        String ip = getClientIp(request);
        postDto.setIp(ip);
        
        // 현재 날짜 설정
        postDto.setCreateat(LocalDate.now());
        
        // 초기 값 설정
        postDto.setReadcount(0);
        postDto.setCommentcount(0);
        
        // 언어 코드 대문자로 변환 (DB에 대문자로 저장)
        if (postDto.getLang() != null) {
            postDto.setLang(postDto.getLang().toUpperCase());
        }
        
        log.info("게시글 저장 요청: {}", postDto);
        
        // 서비스를 통해 게시글 저장
        return service.savePost(postDto)
                .thenApply(savedPost -> {
                    Map<String, Object> responseData = new HashMap<>();
                    responseData.put("postIdx", savedPost.getIdx());
                    log.info("게시글 저장 성공: idx={}", savedPost.getIdx());
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(ApiResponse.success("게시글이 성공적으로 저장되었습니다.", responseData));
                })
                .exceptionally(e -> {
                    if (e.getCause() instanceof BusinessException) {
                        throw (BusinessException) e.getCause();
                    }
                    log.error("게시글 저장 중 오류 발생", e);
                    throw new BusinessException("게시글 저장 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
                });
    }
    
    // 클라이언트 IP 주소 가져오기
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        return ip;
    }
}