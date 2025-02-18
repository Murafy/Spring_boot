package org.iclass.demo.lombok;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {

	private UserDao userDao;
	
	public void logicTest() {
		System.out.println(userDao);
		userDao.daoTest();
	}
}
