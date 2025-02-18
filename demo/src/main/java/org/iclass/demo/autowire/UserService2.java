package org.iclass.demo.autowire;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserService2 {
	private UserDao2 userDao; // userDao 의존관계 선언 
	
	public void logicTest() {
		System.out.println(userDao);
		userDao.daoTest();
	}
}
