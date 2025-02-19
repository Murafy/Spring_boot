package org.iclass.BeanEx;

import org.iclass.BeanEx.setter.YourTestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BeanExApplication2setter {
// BeanExApplication 에서는 MyTestController커스텀 생성자를 사용하여 객체 변수를 초기화 했음
// 여기서 테스트하는 YourTestController2setter 는 기본생성자로 함 
	public static void main(String[] args) {
		ApplicationContext context = 
		SpringApplication.run(BeanExApplication2setter.class, args);
		
		// 사용자 요청은 컨트롤러가 처리하므로 myTestController bean 만 가져와 메서드 실행
		// YourTestController controllor = context.getBean(YourTestController.class);  데이터 타입으로 Bean을 불러옴
		   
		   																
		   YourTestController controllor = (YourTestController) context.getBean("yourTestController");
		   				// 문자열로 bean을 불러옴 첫글자를 소문자로 해야하며 문자열 기반으로 불러오기때문에 casting을 해줘야한다
		   
		   
		controllor.test();
	}

}
