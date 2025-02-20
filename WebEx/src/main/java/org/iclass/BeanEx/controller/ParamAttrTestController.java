package org.iclass.BeanEx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import Dto.PageDto;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class ParamAttrTestController {
	
	@PostMapping("/board/search")
	public String search(PageDto dto, RedirectAttributes reAttr) {
		// Post임으로 return은 redirect 일때 
		log.info("POST 요청 -> /board/search 파라미터 {} , {}: ",dto.getPage(),dto.getKeyword());
		// 리다이렉트 할때에도 Attribute(파라미터) 를 줄수있을까 ? (JSP는 불가능)
		reAttr.addAttribute("page",dto.getPage());
		reAttr.addAttribute("keyword",dto.getKeyword());
		reAttr.addFlashAttribute("message",dto.getKeyword()+"검색 완료했습니다.");
		return "redirect:/board/search";
	}
	
	@GetMapping("/board/search")
	public String searchlist(@ModelAttribute("pageDto") PageDto dto) {
		log.info("GET /board/search 파라미터 : {} , {}", dto.getPage(), dto.getKeyword());
		//model.addAttribute("pageDto",dto); //ModelAttribute ("pageDto") 사용가능
		return "board/search";
	}
	
	
	/*
	 * @PostMapping("/board/search") public String search(int page,String keyword,
	 * RedirectAttributes reAttr) { // Post임으로 return은 redirect 일때
	 * log.info("POST 요청 -> /board/search 파라미터 {} , {}: ",page,keyword); // 리다이렉트
	 * 할때에도 Attribute(파라미터) 를 줄수있을까 ? (JSP는 불가능)
	 * //model.addAttribute("page",pageDto.getPage());
	 * reAttr.addAttribute("page",page); reAttr.addAttribute("keyword",keyword);
	 * return "redirect:/board/search"; }
	 * 
	 * @GetMapping("/board/search") public String searchlist(
	 * 
	 * @RequestParam(defaultValue = "1" ) int page, String keyword, Model model) {
	 * log.info("GET /board/search 파라미터 : {} , {}", page, keyword);
	 * model.addAttribute("page",page); model.addAttribute("keyword",keyword);
	 * return "board/search"; }
	 */
			
	
	@GetMapping("/board/list")
	public String list(
			//@RequestParam(defaultValue = "1")
			@ModelAttribute(name = "page") int page) {
		log.info("GET 요청 list 파라미터 : {}",page);
		
		return "board/list";
	}
	
	@GetMapping("/board/write")
	// 127.0.0.1:8085/board/write?code=XYZ
	// 파라미터값은 XYZ 이는 write 메서드의 인자 code에 입력됨
	// 파라미터값이 저장된 변수 code를 Attribute로 저장하고 model을 이용하여
	// View 로 넘겨준다, Attribute 속성으로 넘어간 파라미터값은 th:value="${code}"에 입력되어
	// View 화면에 그대로 출력된다
	public String write(String code,Model model) {
		model.addAttribute("code",code);
		return "board/write";
	}
	
	@PostMapping("/board/write")
	public String save(String title, String content) {
		log.info("Post 요청 파라미터 : {}, {}",title, content);
		return "redirect:/";
	}
	
}
