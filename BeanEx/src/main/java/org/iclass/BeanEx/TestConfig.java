package org.iclass.BeanEx;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class TestConfig {
// 다른 외부라이브러리에서 메서드 실행 후 리턴 결과를 Bean 으로 만드는 작업에 좋다 
	
	@Bean // 메서드 리턴값으로 bean 을 생성함 
	 List<String> nuz(){ //bean 이 생성될때 메서드 이름과 동일하게 만들어진다.
		log.info("TestConfig 의 nuz() 메서드");
		return List.of("민지","하니","다니엘","해린","혜인");
	}
}
