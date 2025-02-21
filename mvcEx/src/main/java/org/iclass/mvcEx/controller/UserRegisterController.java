package org.iclass.mvcEx.controller;



import org.iclass.mvcEx.dto.UserAccount;
import org.iclass.mvcEx.service.UserRegisterService;
import org.springframework.stereotype.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;


@Slf4j
@Controller
@AllArgsConstructor
public class UserRegisterController {
	private UserRegisterService service;
	
	@GetMapping("/register")
	public String register() {
		return "register";
	}
	
	@PostMapping("register")
	public String register (UserAccount dto, HttpSession session, RedirectAttributes reAttr) {
		int register = service.register(dto);
		log.info("dto 데이터 확인 : {}", register);
		if(register != 0) {
			//session.setAttribute("register", register);
			reAttr.addFlashAttribute("yeah","y");
			return "redirect:login";
		}else {
			reAttr.addFlashAttribute("fail","n");
			return "redirect:register";
		}
	}
	
}
