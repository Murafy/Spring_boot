package org.iclass.BeanEx;

import org.iclass.constructor.MyTestControllor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BeanExApplication {

	public static void main(String[] args) {
		ApplicationContext context = 
		SpringApplication.run(BeanExApplication.class, args);
		
		// 사용자 요청은 컨트롤러가 처리하므로 myTestController bean 만 가져와 메서드 실행
		MyTestControllor controllor = context.getBean(MyTestControllor.class);
		controllor.test();
	}

}
