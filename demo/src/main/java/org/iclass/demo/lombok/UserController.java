package org.iclass.demo.lombok;

public class UserController {
	private UserService userService;
	
	public void test() {
		System.out.println("Lombok 사용 유저 컨트롤러에서 찍힌 값 : " +userService);
		userService.logicTest();
	}
	
}
