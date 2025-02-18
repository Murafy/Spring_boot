package org.iclass.demo.review;

//Controller  -   Service     - Dao ----> 데이터베이스
public class UserService {
	// 서비스는 Dao 타입의 객체를 사용함 ( 의존성 )
	//			   ㄴ daoTest() 메소드 실행 테스트
	private UserDao userDao;
	
	public UserService(UserDao dao) {
		this.userDao = dao;
	}
	
	public void logicTest() {
		System.out.println("유저 서비스에서 찍힌 값 : " + userDao);
		userDao.daoTest();
	}
}
