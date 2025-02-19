package org.iclass.BeanEx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;


// web MVC 에서는 Controller 가 url 요청을 받아서 해당되는 view 를 지정함
// Controller 와 View 의 매핑.
@Slf4j
@Controller
public class TestController {

	// Get 요청 처리. URL(servlet path) 은 join
	@GetMapping("join") // a태그의 URL 과 @GetMapping 인자값 매핑
	public String join() {
		return "join"; // html 파일 리턴
	}
	
	@GetMapping("login")
	public String login() {
		return "login"; 
	}
}
