package org.iclass.BeanEx.controller;

import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import Dto.TestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Slf4j
@Controller
public class AttributeTestController {
	//화면(View) 에 전달할 데이터를 저장하는법
	// View 는 서버사이드 템플릿 엔진인 Thymeleaf
	
	@GetMapping("/community/list")
	public String list(Model model) {
		List<String> list = new ArrayList<>();
		list.add("민지");
		list.add("해린");
		list.add("카리나");
		list.add("호진");
		
		//View list.html 에 전달되는 데이터
		model.addAttribute("page",3);
		model.addAttribute("testDto",new TestDto());
		model.addAttribute("list", list);
		return "community/list";
	}
	
	@GetMapping("/community/read")
	public String read(Model model) {
		TestDto dto = new TestDto("해린",23,"서울","여자");
		
		model.addAttribute("dto", dto);
		return "/community/read";
	}
	
	@GetMapping("exercise")
	public String exercise(Model model) {
		List<TestDto> list = new ArrayList<>();
		list.add(new TestDto("홍길동",22,"서울","남자"));
		list.add(new TestDto("이나연",23,"서울","여자"));
		list.add(new TestDto("해린",25,"서울","여자"));
		
		model.addAttribute("dto",list);
		return "exercise";
	}
	
}
