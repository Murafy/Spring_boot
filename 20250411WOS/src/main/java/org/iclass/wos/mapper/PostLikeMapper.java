package org.iclass.wos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iclass.wos.dto.PostLikeDto;
import org.iclass.wos.dto.PostLikeCountDto;

@Mapper
public interface PostLikeMapper {
    // 좋아요 정보 조회
    PostLikeDto getLikeByPostAndUser(@Param("postId") Integer postId, @Param("userId") String userId);
    
    // 좋아요 추가
    void insertLike(PostLikeDto like);
    
    // 좋아요 삭제
    void deleteLike(@Param("postId") Integer postId, @Param("userId") String userId);
    
    // 게시글의 좋아요 개수 조회
    Integer getLikeCount(@Param("postId") Integer postId);
    
    // 좋아요 개수 정보 조회
    PostLikeCountDto getLikeCountInfo(@Param("postId") Integer postId);
    
    // 좋아요 개수 직접 증가
    void increaseLikeCount(@Param("postId") Integer postId);
    
    // 좋아요 개수 직접 감소
    void decreaseLikeCount(@Param("postId") Integer postId);
}