package org.iclass.BeanEx.setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
@Controller
public class YourTestController {

	private YourTestService service;
	
	public YourTestController() {
		log.info("YourTestController 기본생성자 실행합니다");
	}
	public void test () {
		log.info("★★★사용자 요청을 처리합니다.★★★");
		service.logicTest();
	}
	
	// setter 기본생성자여서 씀
	@Autowired //의존 관계 Bean 자동 주입 (Setter 메서드 호출 시 YourTestService Bean 객체 전달)
	public void setService(YourTestService service) {
		this.service = service;
	}
}
