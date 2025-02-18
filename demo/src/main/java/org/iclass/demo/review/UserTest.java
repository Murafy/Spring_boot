package org.iclass.demo.review;

public class UserTest {
	// Controller  -   Service     - Dao ----> 데이터베이스
	public static void main(String[] args) {
		
		//Controller, Service, Dao 는 각각 역활이 있으며 분리되어 작성됨
		// 이 때 각각 객체가 생성되어 다른 객체 생성시 초기화로 사용되고있음 
		UserDao userDao = new UserDao();
		UserService userService = new UserService(userDao);
		UserController userController = new UserController(userService);
		
		userController.test();
		
	}

}
