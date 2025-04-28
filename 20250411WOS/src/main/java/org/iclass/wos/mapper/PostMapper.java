package org.iclass.wos.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iclass.wos.dto.PostDto;

@Mapper
public interface PostMapper {
	// 페이지네이션을 위한 총 게시글수 파악 (언어별) 인자로 받은 매개변수명과 
	// sql 매개변수 명이 다름으로 @Param을 사용해서 자리 찾아주기
	int getAllCount(@Param("lang") String language); 
	
	// PostService에서 글목록 가져오면서 getAllCount 메서드로 총 파악된 게시글수 정보를 기반으로 페이지네이션 (언어 포함)
	List<PostDto> selectPageList(Map<String , Object> map);
	
	
	// 글읽기 (상세페이지)
	PostDto readPage (Map<String,String>map);
	
	// 조회수 증가
	void viewCount (int idx);
	
	// 글쓰기 (새 게시글 저장)
    int insertPost(PostDto postDto);
	
}
