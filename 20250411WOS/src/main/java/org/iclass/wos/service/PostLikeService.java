package org.iclass.wos.service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.iclass.wos.dto.PostLikeCountDto;
import org.iclass.wos.dto.PostLikeDto;
import org.iclass.wos.dto.PostLikeResponseDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.PostLikeMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostLikeService {
    
    private final PostLikeMapper likeMapper;
    private final ExecutorService executorService;
    
    public PostLikeService(PostLikeMapper likeMapper) {
        this.likeMapper = likeMapper;
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    /**
     * 사용자가 게시글에 좋아요를 눌렀는지 확인합니다.
     */
    @Cacheable(value = "likeStatus", key = "'post_' + #postId + '_user_' + #userId")
    public CompletableFuture<Boolean> isPostLikedByUser(Integer postId, String userId) {
        if (postId == null) {
            throw new BusinessException("게시글 ID는 필수 값입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("사용자 ID는 필수 값입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                PostLikeDto like = likeMapper.getLikeByPostAndUser(postId, userId);
                return like != null;
            } catch (Exception e) {
                log.error("좋아요 상태 확인 중 오류: postId={}, userId={}", postId, userId, e);
                throw new BusinessException("좋아요 상태 확인 중 오류가 발생했습니다.", ErrorCode.DATABASE_ERROR);
            }
        }, executorService);
    }
    
    /**
     * 게시글의 좋아요 개수를 조회합니다.
     */
    @Cacheable(value = "likeCount", key = "'post_' + #postId")
    public CompletableFuture<Integer> getPostLikeCount(Integer postId) {
        if (postId == null) {
            throw new BusinessException("게시글 ID는 필수 값입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 집계 테이블에서 개수 조회
                PostLikeCountDto countInfo = likeMapper.getLikeCountInfo(postId);
                
                if (countInfo != null) {
                    return countInfo.getLikeCount();
                } else {
                    // 집계 정보가 없으면 직접 카운트
                    return likeMapper.getLikeCount(postId);
                }
            } catch (Exception e) {
                log.error("좋아요 개수 조회 중 오류: postId={}", postId, e);
                throw new BusinessException("좋아요 개수 조회 중 오류가 발생했습니다.", ErrorCode.DATABASE_ERROR);
            }
        }, executorService);
    }
    
    /**
     * 좋아요 상태를 변경합니다. (좋아요 추가 또는 삭제)
     */
    @Transactional
    @CacheEvict(value = {"likeStatus", "likeCount"}, allEntries = true)
    public CompletableFuture<PostLikeResponseDto> togglePostLike(Integer postId, String userId) {
        if (postId == null) {
            throw new BusinessException("게시글 ID는 필수 값입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (userId == null || userId.trim().isEmpty()) {
            throw new BusinessException("사용자 ID는 필수 값입니다.", ErrorCode.INVALID_REQUEST);
        }
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 현재 좋아요 상태 확인
                PostLikeDto existingLike = likeMapper.getLikeByPostAndUser(postId, userId);
                boolean isCurrentlyLiked = existingLike != null;
                
                if (isCurrentlyLiked) {
                    // 좋아요가 이미 있으면 삭제
                    likeMapper.deleteLike(postId, userId);
                    
                    // 트리거가 없는 경우 직접 개수 감소
                    likeMapper.decreaseLikeCount(postId);
                    
                    // 새로운 개수 가져오기
                    PostLikeCountDto countInfo = likeMapper.getLikeCountInfo(postId);
                    Integer newCount = countInfo != null ? countInfo.getLikeCount() : 0;
                    
                    return PostLikeResponseDto.builder()
                            .success(true)
                            .liked(false)
                            .count(newCount)
                            .build();
                } else {
                    // 좋아요가 없으면 추가
                    PostLikeDto newLike = PostLikeDto.builder()
                            .postId(postId)
                            .userId(userId)
                            .build();
                    
                    likeMapper.insertLike(newLike);
                    
                    // 트리거가 없는 경우 직접 개수 증가
                    likeMapper.increaseLikeCount(postId);
                    
                    // 새로운 개수 가져오기
                    PostLikeCountDto countInfo = likeMapper.getLikeCountInfo(postId);
                    Integer newCount = countInfo != null ? countInfo.getLikeCount() : 1;
                    
                    return PostLikeResponseDto.builder()
                            .success(true)
                            .liked(true)
                            .count(newCount)
                            .build();
                }
            } catch (Exception e) {
                log.error("좋아요 토글 처리 중 오류 발생: postId={}, userId={}", postId, userId, e);
                throw new BusinessException("좋아요 처리 중 오류가 발생했습니다.", ErrorCode.DATABASE_ERROR);
            }
        }, executorService);
    }
    
    // 서비스 종료 시 실행되는 메서드
    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}