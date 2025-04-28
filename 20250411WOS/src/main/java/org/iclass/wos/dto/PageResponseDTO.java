package org.iclass.wos.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class PageResponseDTO {
	private int totalCount; // 전체 글 개수
	private int totalPages;
	
	private int startPage;
	private int endPage;
	
	// 핵심! 게시글이 저장되어있는 PostDto를 List 형태로 받음
	private List<PostDto> list; 
}
