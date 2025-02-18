package org.iclass.demo.lombok;

import org.iclass.demo.review.UserController;
import org.iclass.demo.review.UserDao;
import org.iclass.demo.review.UserService;

public class UserTest2 {

	public static void main(String[] args) {
		UserDao userDao = new UserDao();
		UserService userService = new UserService(userDao);
		UserController userController = new UserController(userService);
		
		userController.test();
		
	}

	}


