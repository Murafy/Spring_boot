package org.iclass.wos.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.iclass.wos.dto.ApiResponse;
import org.iclass.wos.dto.PostLikeRequestDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/post")
public class PostLikeController {
    
    private final PostLikeService likeService;
    
    /**
     * 게시글의 좋아요 개수를 조회합니다.
     */
    @GetMapping("/likes")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getPostLikes(@RequestParam("idx") Integer postId) {
        log.info("게시글 좋아요 개수 조회 요청: postId={}", postId);
        
        return likeService.getPostLikeCount(postId)
            .thenApply(count -> {
                Map<String, Object> response = new HashMap<>();
                response.put("count", count);
                log.info("게시글 좋아요 개수 조회 결과: postId={}, count={}", postId, count);
                return ResponseEntity.ok(ApiResponse.success(response));
            })
            .exceptionally(e -> {
                if (e.getCause() instanceof BusinessException) {
                    throw (BusinessException) e.getCause();
                }
                throw new BusinessException("좋아요 개수 조회 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            });
    }
    
    /**
     * 현재 사용자가 특정 게시글에 좋아요를 눌렀는지 확인합니다.
     */
    @GetMapping("/like/status")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> getLikeStatus(
            @RequestParam("idx") Integer postId) {
        
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            Map<String, Object> response = new HashMap<>();
            response.put("liked", false);
            response.put("message", "로그인이 필요합니다.");
            return CompletableFuture.completedFuture(ResponseEntity.ok(ApiResponse.success(response)));
        }
        
        String userId = authentication.getName();
        log.info("게시글 좋아요 상태 확인 요청: postId={}, userId={}", postId, userId);
        
        return likeService.isPostLikedByUser(postId, userId)
            .thenApply(isLiked -> {
                Map<String, Object> response = new HashMap<>();
                response.put("liked", isLiked);
                log.info("게시글 좋아요 상태 확인 결과: postId={}, userId={}, liked={}", postId, userId, isLiked);
                return ResponseEntity.ok(ApiResponse.success(response));
            })
            .exceptionally(e -> {
                if (e.getCause() instanceof BusinessException) {
                    throw (BusinessException) e.getCause();
                }
                throw new BusinessException("좋아요 상태 확인 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            });
    }
    
    /**
     * 게시글 좋아요 상태를 토글합니다. (좋아요 추가 또는 삭제)
     */
    @PostMapping("/like")
    public CompletableFuture<ResponseEntity<ApiResponse<Map<String, Object>>>> toggleLike(
            @RequestBody PostLikeRequestDto requestDto) {
        
        // 인증된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            throw new BusinessException("로그인이 필요합니다.", ErrorCode.UNAUTHORIZED);
        }
        
        String userId = authentication.getName();
        Integer postId = requestDto.getPostIdx();
        
        log.info("게시글 좋아요 토글 요청: postId={}, userId={}", postId, userId);
        
        return likeService.togglePostLike(postId, userId)
            .thenApply(result -> {
                Map<String, Object> response = new HashMap<>();
                
                if (result.getSuccess()) {
                    response.put("liked", result.getLiked());
                    response.put("count", result.getCount());
                    log.info("게시글 좋아요 토글 성공: postId={}, userId={}, liked={}, count={}", 
                             postId, userId, result.getLiked(), result.getCount());
                    
                    return ResponseEntity.ok(ApiResponse.success(response));
                } else {
                    log.warn("게시글 좋아요 토글 실패: postId={}, userId={}, message={}", 
                             postId, userId, result.getMessage());
                    
                    throw new BusinessException(result.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR);
                }
            })
            .exceptionally(e -> {
                if (e.getCause() instanceof BusinessException) {
                    throw (BusinessException) e.getCause();
                }
                throw new BusinessException("좋아요 처리 중 오류가 발생했습니다.", ErrorCode.INTERNAL_SERVER_ERROR);
            });
    }
}