package org.iclass.demo.review;

// Run As -> Java Application 으로 실행합니다.
public class UserTest {

	public static void main(String[] args) {
		
		// Controller, Service, Dao 는 각각 역할이 있으며 분리되어 작성됩니다.
		// 이 때 각각 객체가 생성되어 다른 객체 생성시 초기화로 사용되고 있음.
		UserDao userDao = new UserDao();
		UserService userService = new UserService(userDao);
		UserController userController = new UserController(userService);
		
		userController.test();

	}

}
