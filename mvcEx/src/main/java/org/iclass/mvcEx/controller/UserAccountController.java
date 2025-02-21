package org.iclass.mvcEx.controller;

import org.iclass.mvcEx.dto.UserAccount;
import org.iclass.mvcEx.service.UserAccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor

public class UserAccountController {
	private UserAccountService service;
	
	// 레이아웃 샘플
	@GetMapping("/sample")
	public String sample() {
		return "sample";
	}
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
	
	@PostMapping("/login")
	public String action(String userid,String password, RedirectAttributes reAttr, HttpSession session) {
		UserAccount account = service.login(userid,password);
		log.info("login 계정 정보 : {} ", account);
		if(account != null) {
			session.setAttribute("account", account);
			return "redirect:/";
		}else {
			reAttr.addFlashAttribute("fale","y");
			return "redirect:login";
		}
	}
	
	@GetMapping("/logout")
	public String logout(HttpSession session,RedirectAttributes reAttr) {
		session.invalidate();
		reAttr.addFlashAttribute("message","로그아웃 완료");
		return "redirect:/";
	}
}
