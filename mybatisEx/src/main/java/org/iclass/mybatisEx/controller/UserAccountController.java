package org.iclass.mybatisEx.controller;

import java.util.HashMap;
import java.util.Map;

import org.iclass.mybatisEx.dto.UserAccount;
import org.iclass.mybatisEx.mapper.UserAccountMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@Slf4j
public class UserAccountController {
	private UserAccountMapper accountMapper;
	
	public UserAccountController(UserAccountMapper accountMapper) {
		this.accountMapper=accountMapper;
	}
	
	@PostMapping("login")
	public String login(String userid, String password, RedirectAttributes reAttr) {
		Map<String,String> map = new HashMap<>();
		map.put("userid", userid);
		map.put("password", password);
		UserAccount account = accountMapper.selectForLogin(map);
		log.info("login 계정 정보 : {}",account);
		if(account != null) {
			return "redirect:/";
		}else {
			reAttr.addFlashAttribute("fail","y");
			// flash는 get 매핑을 안거치고 바로 view로 전송 가능
			// '[[${fail}]]' 으로 출력
			return "redirect:login";
		}
	}
	
	@GetMapping("login")
	public String login(Model model) {
		
		
		return "login";
	}
	
	
}
