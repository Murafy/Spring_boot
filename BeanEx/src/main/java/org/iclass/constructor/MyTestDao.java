package org.iclass.constructor;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MyTestDao { // MVC 모델2 에서 SQL 실행
	
	public void mybatis() { // Dao는 MVC 모델2에서 SQL 실행함
		log.info("■■■MyTestDao Bean 확인 : mybatis 메서드 실행■■■");
	}
	public MyTestDao() {
		log.info("■■■MyTestDao bean 이 생성됩니다.■■■");
	}
}
