package org.iclass.mybatisEx.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.iclass.mybatisEx.dto.Customer;

// mapper XML에서 실행 SQL 의 id를 메서드로 정의함
// 인터페이스는 구현클래스가 필요함. 구현클래스를 마이바티스와 스프링 프레임윅이 알아서 만들어줌 
// 인터페이스로 규칙만 전달.
@Mapper
public interface CustomerMapper {
	Customer selectByPk(String customerid);
	List<Customer> selectAll();
	int insert(Customer dto);
	int update(Customer dto);
	int delete(String customerid);
}
