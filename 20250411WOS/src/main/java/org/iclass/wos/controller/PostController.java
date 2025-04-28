package org.iclass.wos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PostController {

	// 엔드포인트를 매개변수로 받아 post(view)를 리턴한다
	// view는 자바스크립트로 restapi 처리를 한다.
    @GetMapping("/main/board/{language}")
    public String showBoard(@PathVariable String language) {
        return "post/post"; 
    }
    // 상세글 보기 
    @GetMapping("/read")
    public String showRead() {
        return "post/read";
    }
    // 글쓰기 페이지
    @GetMapping("/write")
    public String showWrite(@RequestParam(name = "language", required = false) String language, Model model) {   
        // 언어 정보를 모델에 추가 (뷰에서 사용하기 위함)
        model.addAttribute("language", language);
        
        return "post/write";
    }
    
}