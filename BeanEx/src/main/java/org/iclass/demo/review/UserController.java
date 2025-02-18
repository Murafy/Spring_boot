package org.iclass.demo.review;

//Controller    -   Service    -  Dao -----> 데이터베이스
//(http 요청처리)    (비지니스로직 실행)
public class UserController {
	//컨트롤러는 서비스 타입의 객체를 사용합니다.(의존성)
	//               ㄴ logicTest() 메소드 실행 테스트
	private UserService userService;      //UserController의 멤버 변수
	
	// 객체 생성하면서 userSevice 를 초기화
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	public void test() {
		System.out.println(userService);
		userService.logicTest();
	}
}
