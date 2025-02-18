package org.iclass.demo.review;

//Controller    -   Service    -  Dao -----> 데이터베이스
//(http 요청처리)    (비지니스로직 실행)
public class UserService {
	//서비스는 Dao 타입의 객체를 사용합니다.(의존성)
	//				ㄴ daoTest() 메소드 실행 테스트
	private UserDao userDao ;
	
	// 객체 생성하면서 userDao 를 초기화
	public UserService(UserDao dao) {
		this.userDao = dao;
	}
	
	public void logicTest() {
		System.out.println(userDao);
		userDao.daoTest();
	}
	
}
