package org.iclass.BeanEx.setter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
//@Component
@Service
public class YourTestService {

	private YourTestDao dao;
	
	public YourTestService() {
		log.info("YourTestService 기본 생성자 실행합니다");
	}
	public void logicTest() {
		log.info("◆◆◆비지니스 로직이 실행 됩니다.◆◆◆");
		dao.mybatis();
	}
	@Autowired //의존관계 bean 자동주입
	public void setDao(YourTestDao dao) {
		this.dao = dao;
	}
}
