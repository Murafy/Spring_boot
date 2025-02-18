package org.iclass.demo.lombok;

public class UserTest2 {

	public static void main(String[] args) {
		
		UserDao userDao = new UserDao();    //기본생성자
		UserService userService = new UserService(userDao);
		UserController userController = new UserController(userService);
		userController.test();

	}

}
