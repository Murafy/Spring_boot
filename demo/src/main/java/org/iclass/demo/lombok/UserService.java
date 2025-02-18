package org.iclass.demo.lombok;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserService {
	private UserDao userDao;
	
	public void logicTest() {
		System.out.println("유저 서비스에서 찍힌 값 : " + userDao);
		userDao.daoTest();
	}
}
