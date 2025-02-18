package org.iclass.demo.autowire;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Component
public class UserController2 {
	
	private UserService2 userService;
	
	public void test() {
		System.out.println(userService);
		userService.logicTest();
	}
	
}
