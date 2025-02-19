package org.iclass.BeanEx;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class BeanExAppliction3 {
	public static void main(String[] args) {
		SpringApplication.run(BeanExAppliction3.class, args); // 스프링 부트 시작 인자로 넘겨줌 
		
		@SuppressWarnings("resource") // 경고알림 끔
		ApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class);
		// List<String> temp = (List<String>) context.getBean("nuz"); // @Bean 으로 만들어진 Bean 객체 Get
		// log.info("@Bean 으로 만들어진 List의 값 : {}",temp);
		@SuppressWarnings("unchecked")
		List<String> temp = (List<String>) context.getBean(java.util.List.class); // @Bean 으로 만들어진 Bean 객체 Get
		log.info("@Bean 으로 만들어진 List의 값 : {}, {}",temp, temp.getClass().getName());
	}
}
