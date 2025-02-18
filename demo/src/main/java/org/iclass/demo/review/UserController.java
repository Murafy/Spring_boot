package org.iclass.demo.review;

//Controller  -   Service     - Dao ----> 데이터베이스
public class UserController {
	// 컨트롤러는 서비스 타입의 객체를 사용함 ( 의존성 )
	//				  ㄴ logicTest() 메소드 실행 테스트
	private UserService userService;
	
	// 커스텀 생성자로 초기화 
	public UserController (UserService userController) {
		this.userService = userController;
	}
	public void test() {
		System.out.println("유저 컨트롤러에서 찍힌 값 : " +userService);
		userService.logicTest();
	}
}
