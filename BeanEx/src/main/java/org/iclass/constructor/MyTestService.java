package org.iclass.constructor;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTestService { // Dao는 MVC 모델2에서 비지니스 로직을 실행함

	// 의존성 선언
	private MyTestDao dao;
	
	// 생성자 실행시 자동으로 MyTestDao 타입 bean 을 찾아서 초기화 실행
	public MyTestService(MyTestDao dao) {
		this.dao = dao; // 의존성 객체 초기화 (롬복안써서 씀)
		log.info("◆◆◆의존성 빈 dao 확인 ; {}◆◆◆", this.dao);
	}
	
	public void logicTest() {
		log.info("◆◆◆비지니스 로직이 실행 됩니다.◆◆◆");
		dao.mybatis();
	}
}
