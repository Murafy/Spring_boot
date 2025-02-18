package org.iclass.demo;

import org.iclass.demo.autowire.UserController2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

// 스프링 부트 프로젝트는 여기서 실행해야 합니다. Run As -> Spring Boot App
@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
// 스프링부트에서는 자동 설정이 대부분입니다. run 메소드 실행결과를 리턴 받아 context 객체에
//		저장하여 사용할 수 있다. context 는 Bean 저장소에 접근하기 위한 객체입니다.
		ApplicationContext context =
		SpringApplication.run(DemoApplication.class, args);
		
// 아래 설정은 수동이므로 스프링 부트에서는 사용하지 않음.		
//		AbstractApplicationContext context =
//				new AnnotationConfigApplicationContext("설정파일클래스이름");	
		
		System.out.println("[[[스프링 컨테이너에 저장된 스프링빈의 이름 확인]]]");
		String[] beans = context.getBeanDefinitionNames();
		for (String bean : beans) {
			System.out.println("    - 빈 이름 : " + bean);
		}
		
		UserController2 controller = context.getBean(UserController2.class);
		controller.test();
		
//		context.close();
	}
	

}
