package org.iclass.wos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;


@Controller
@Slf4j
public class MainController {
	// 메인 페이지
	@GetMapping("/main")
	private String main() {
		return "main";
	}
	
	// 회원 가입 페이지
	@GetMapping("main/regist")
	private String regist() {
		return "regist";
	}
	
	
	// 코드 테스트 페이지
	@GetMapping("main/code")
	private String code() {
		return "compiler"; 
	}
	
	// 어드민 페이지
	@GetMapping("main/admin")
	public String admin() {
		return "admin";
	}
	
	
}
