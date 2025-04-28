package org.iclass.wos.service;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.iclass.wos.dto.PageResponseDTO;
import org.iclass.wos.dto.PostDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.mapper.PostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PostService { 
    private final Map<String, PostMapper> mapperMap; // PostMapper 사용
    private final ExecutorService executorService;

    public PostService(PostMapper postMapper) { 
    	// map의 key는 URL 에서 분리한 언어영역에 해당하는 URL 과 비교함으로써 지원하는 언어인지 확인한다
    	// 쿼리문의 매개변수 값으로 입력되는값은 language 임.
        mapperMap = new HashMap<>();
        mapperMap.put("java", postMapper);
        mapperMap.put("javascr", postMapper);
        mapperMap.put("orasql", postMapper);
        mapperMap.put("react", postMapper);
        mapperMap.put("htmlcss", postMapper);
        mapperMap.put("springboot", postMapper);
        // mapperMap.put("python", pythonPostMapper);
        
        this.executorService = Executors.newFixedThreadPool(4);
    }

    // 페이지 네이션 (비동기 방식) - 언어 정보 파라미터 추가
    public CompletableFuture<PageResponseDTO> getPageListAsync(String language, int currentPage) {
        return CompletableFuture.supplyAsync(() -> {
        	// 위 Map에서 URL의 언어영역에 해당하는 URL값과 KEY값을 비교후 일치하면 KEY의 VALUE 가져옴
        	PostMapper mapper = mapperMap.get(language.toLowerCase()); 
            if (mapper == null) {
                throw new BusinessException("지원하지 않는 언어입니다: " + language, ErrorCode.INVALID_LANGUAGE);
            }

            // 페이지 번호 유효성 검사
            if (currentPage < 1) {
                throw new BusinessException("페이지 번호는 1 이상이어야 합니다.", ErrorCode.INVALID_REQUEST);
            }

            Map<String, Object> map = new HashMap<>(); // 파라미터 타입을 Object로 변경
            int pageSize = 9;
            int startNo = (currentPage - 1) * pageSize + 1;
            int endNo = startNo + (pageSize - 1);
            map.put("startNo", startNo);
            map.put("endNo", endNo);
            map.put("language", language.toUpperCase()); // LANG 컬럼 값과 일치하도록 대문자로 변환

            int totalCount = mapper.getAllCount(language.toUpperCase()); // 언어 정보 전달
            
            // 존재하지 않는 페이지 요청 처리
            double temp = (double) totalCount / pageSize;
            int totalPages = (int) Math.ceil(temp);
            
            if (totalCount > 0 && currentPage > totalPages) {
                throw new BusinessException(
                    "요청한 페이지가 존재하지 않습니다. (전체 페이지: " + totalPages + ")",
                    ErrorCode.RESOURCE_NOT_FOUND
                );
            }
            
            int startPage = (currentPage - 1) / 10 * 10 + 1;
            int endPage = startPage + (10 - 1);
            endPage = Math.min(totalPages, endPage);

            return PageResponseDTO.builder()
                    .totalCount(totalCount)
                    .totalPages(totalPages)
                    .startPage(startPage)
                    .endPage(endPage)
                    .list(mapper.selectPageList(map)) // map에 언어 정보 포함
                    .build();
        }, executorService);
    }
    
    // 게시글 상세페이지
    @Autowired
    private PostMapper mapper;
    public CompletableFuture<PostDto> read(String language, int idx) {
        return CompletableFuture.supplyAsync(() -> {
            // 언어 유효성 검사
            if (!mapperMap.containsKey(language.toLowerCase())) {
                throw new BusinessException("지원하지 않는 언어입니다: " + language, ErrorCode.INVALID_LANGUAGE);
            }
            
            // 조회수 증가
            mapper.viewCount(idx);
            
            // 게시글 조회
            Map<String,String> map = new HashMap<>();
            map.put("language", language.toUpperCase()); // 대문자 변환
            map.put("idx", String.valueOf(idx));
            
            PostDto post = mapper.readPage(map);
            
            // 게시글 존재 유무 검사
            if (post == null) {
                throw BusinessException.notFound("게시글", idx);
            }
            
            return post;
        }, executorService);
    }
    
    // 게시글 저장 (비동기 방식)
    public CompletableFuture<PostDto> savePost(PostDto postDto) {
        return CompletableFuture.supplyAsync(() -> {
            // 필수 필드 유효성 검사
            if (postDto.getTitle() == null || postDto.getTitle().trim().isEmpty()) {
                throw BusinessException.invalidValue("title", "제목은 필수 입력 항목입니다.");
            }
            
            if (postDto.getContent() == null || postDto.getContent().trim().isEmpty()) {
                throw BusinessException.invalidValue("content", "내용은 필수 입력 항목입니다.");
            }
            
            if (postDto.getLang() == null || postDto.getLang().trim().isEmpty()) {
                throw BusinessException.invalidValue("lang", "언어 카테고리는 필수 입력 항목입니다.");
            }
            
            // 언어 유효성 검사
            if (!mapperMap.containsKey(postDto.getLang().toLowerCase())) {
                throw new BusinessException(
                    "지원하지 않는 언어입니다: " + postDto.getLang(), 
                    ErrorCode.INVALID_LANGUAGE
                );
            }
            
            // 매퍼를 통해 DB에 게시글 저장
            int result = mapper.insertPost(postDto);
            
            if (result == 0) {
                throw new BusinessException(
                    "게시글 저장에 실패했습니다.", 
                    ErrorCode.INTERNAL_SERVER_ERROR
                );
            }
            
            log.info("게시글 저장 완료: {}", postDto);
            
            return postDto;
        }, executorService);
    }
    
    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}