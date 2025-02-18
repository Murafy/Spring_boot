package org.iclass.demo.lombok;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserController {
	
	private UserService userService;
	
	public void test() {
		System.out.println(userService);
		userService.logicTest();
	}
}
