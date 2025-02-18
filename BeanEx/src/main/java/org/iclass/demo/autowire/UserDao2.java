package org.iclass.demo.autowire;

import org.springframework.stereotype.Component;


@Component
public class UserDao2 {
	
	
	public void daoTest() {
		System.out.println("스프링의 자동 주입 기능입니다.");
	}
}
