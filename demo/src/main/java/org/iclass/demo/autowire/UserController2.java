package org.iclass.demo.autowire;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
@AllArgsConstructor
@Component
public class UserController2 {
	private UserService2 userService; // userService 의존관계 선언
	
	public void test() {
		System.out.println(userService);
		userService.logicTest();
	}
}
