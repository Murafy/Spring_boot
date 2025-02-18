package org.iclass.demo.review;

// Controller    -   Service    -  Dao -----> 데이터베이스
// (http 요청처리)    (비지니스로직 실행)
public class UserDao {
	// Dao 타입의 객체는 SQL 을 실행합니다.
	// Jsp에서는 Mybatis SqlSession 객체에 대한 의존성이 생깁니다.
	
	public void daoTest() {
		System.out.println("SQL 을 실행합니다.");
	}

}
