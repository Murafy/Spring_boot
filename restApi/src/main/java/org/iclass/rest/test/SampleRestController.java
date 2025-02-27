package org.iclass.rest.test;

import java.util.ArrayList;
import java.util.List;

import org.iclass.rest.dto.SampleDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class SampleRestController {
	//db 역할을 하는 list
	private List<SampleDTO> list;
	
	// 생성자는 임시 db역할 
	public SampleRestController() {
		list = new ArrayList<>();
		list.add(new SampleDTO("twice","김땡땡,","1234"));  // 0 (list index)
		list.add(new SampleDTO("wonder","최원더,","234")); // 1
		list.add(new SampleDTO("hong","홍길동,","3456")); // 2
		list.add(new SampleDTO("KGC","강감찬,","7890")); // 3
	}
	
	@GetMapping("/samples")
	public ResponseEntity<List<SampleDTO>> list(String keyword){
									// URL 에 포함된 파라미터 keyword
		// db 구현 : keyword 값이 있을 때는 검색 
		log.info("검색 키워드: ",keyword);
		return ResponseEntity.ok().body(list);
	}
	
	@GetMapping("/samples/one")
	public ResponseEntity<SampleDTO> one(){
		// ok -> 웹 상태코드 (200,404,500 등) / body -> 내용을 리턴한다
		// body 데이터는 자바 객체임 -> 스프링 부트가 json 문자열로 변환하여 전송 
		return ResponseEntity.ok().body(list.get(2));
	}
	
	@PostMapping("/samples")
	// @RequestBody -> 요청으로 날아온 json을 자바 객체로 변환 (처리를 위해)
	public ResponseEntity<?> post(@RequestBody SampleDTO dto){
		// 요청으로 날아온 데이터확인
		log.info("insert 할 dto : {}, {}, {}",dto.getUserid(),dto.getUsername(),dto.getPassword()); 
		try {
			list.add(dto); // 데이터 추가
			log.info("데이터 확인 : {}",list); // list에 추가된 데이터 확인
			return ResponseEntity.ok().body(1); // ok는 상태코드 200을 의미한다
		}catch(Exception e) {
			// 요청 오류시 400 Bad Request 응답 ResponseEntity 생성 (본문 없음)
			return ResponseEntity.badRequest().build();
		}
	}
	
	@GetMapping("/samples/{index}") // 변수값으로 받을수있게 list의 index를 지정함
	// @PathVariable -> 요청을 변수값으로 받을수있게함 
	public ResponseEntity<?> one(@PathVariable int index){
	try {
		return ResponseEntity.ok().body(list.get(index));
	}catch (Exception e){
		// 처리중 오류발생시 500 응답 본문(body)에 "서버오류" 메시지를 담아 ResponseEntity 객체로 반환
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버오류");
	}
	}
	
	
	@PutMapping("/samples")
	public ResponseEntity<?>update(@RequestBody SampleDTO dto) {
		log.info("update 할 dto : {}, {}, {}",dto.getUserid(),dto.getUsername(),dto.getPassword());
		list.add(0,dto); // update 역할 / 0번 index 값을 수정 
		return ResponseEntity.ok().body(1);
	}
	// put 매핑은 path 변수 쓰까 사용 가능 
	// put 매핑은 전체업데이트 , patch 매핑은 일부분 업데이트 
	@PutMapping("/samples/{index}")
	public ResponseEntity<?>update1(@RequestBody SampleDTO dto,@PathVariable int index) {
		log.info("update 할 dto : {}, {}, {}",dto.getUserid(),dto.getUsername(),dto.getPassword());
		list.add(index,dto); // update 역할 / 변수로 index 값을 수정 
		return ResponseEntity.ok().body(list);
	}
	
	@DeleteMapping("/samples/{index}")
	public ResponseEntity<?> delete(@PathVariable int index){
		// delete 역할
		list.remove(index);
		return ResponseEntity.ok().body(index);
	}
	
	
	
}
