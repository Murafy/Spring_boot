package org.iclass.demo.autowire;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Component
public class UserService2 {
	
	private UserDao2 userDao;
	
	public void logicTest() {
		System.out.println(userDao);
		userDao.daoTest();
	}

}
