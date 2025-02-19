package org.iclass.BeanEx.setter;

import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class YourTestDao {
	
	public YourTestDao(){
		log.info("YourTestDao 기본 생성자 실행합니다");
	}
	public void mybatis() { 
		log.info("■■■MyTestDao Bean 확인 : mybatis 메서드 실행■■■");
	}
}
