package org.iclass.BeanEx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Dto.TestDto;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
public class ParameterTestController {
	
	@GetMapping("list")
	public String list(String name, int age) {
		log.info("이름 : {} ",name);
		log.info("나이 : {} ",age);
		return null; // 파일명이 없으면 기본값이 url -> list.html
	}
	
	@GetMapping("listA")
	// 파라미터가 많을 때 받아서 처리하는 코드는 모든 파라미터의 변수를 작성해줘야함 (비효율)
	// @RequestParam(defaultValue = "0") 은 index.html에서 age 파라미터가 없고 dto에는 있을떄 (30줄)
	// null에 기본값을 설정해줘서 오류를 피한다
	public String list(String name, @RequestParam(defaultValue = "0") int age, String address, String gender) {
		log.info("이름 : {} / " + "나이 : {} / "+ "주소 : {} / " + "성별 : {} ", name,age,address,gender);
		return "list";
	}
	
	@GetMapping("listB")
	// 파라미터를 dto에서 처리한뒤 dto로 부터 값을 받아 처리
	public String listB(TestDto dto) {
		log.info("dto 로 파라미터 가져오기 : {} ", dto);
		log.info("이름 : {} / 나이 : {} / 주소 : {} / 성별 : {}", 
				dto.getName(),dto.getAge(),dto.getAddress(),dto.getGender());
		return "list";
	}
	
	
	@GetMapping("write")
	public String write() {
		return "write";
	}

	@PostMapping("write") // Form action 속성 URL 과 @PostMapping 인자값 매핑
	public String save(String title, String content) {
		// 메서드인 변수명 title, content는 view 에서 서버로 보낸 파라미터를 저장함 (name)
		log.info("post 요청을 처리하고 context path 로 리다이렉트");
		log.info("title : {}", title);
		log.info("context : {}", content);
		return "redirect:/"; // "/" 요게 컨텍스트 path 임
	}
	
	@PostMapping("regist")
	public String regist(TestDto dto) {
		log.info("이름 : {} / 나이 : {} / 주소 : {} / 성별 : {}", 
				dto.getName(),dto.getAge(),dto.getAddress(),dto.getGender());
		return "redirect:/";
	}
	
}
