package org.iclass.constructor;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTestControllor { // Dao는 MVC 모델2에서 사용자 요청을 처리함
	// 의존성
	private MyTestService service;
	
	public MyTestControllor(MyTestService service) {
		this.service = service;
		log.info("★★★의존성 빈 service 확인★★★ : {}", this.service);
	}
	
	public void test () {
		log.info("★★★사용자 요청을 처리합니다.★★★");
		service.logicTest();
	}
}

// 사용자 요청 처리	 ->	  비지니스 로직 실행	 ->	    	mybais   ->  Dao 실행
// (DB에 회원추가)  (mybatis에게 insert 실행지시)  (Dao에게 쿼리실행 지시)