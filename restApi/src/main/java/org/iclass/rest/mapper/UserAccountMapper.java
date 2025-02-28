package org.iclass.rest.mapper;


import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.iclass.rest.dto.UserAccount;


@Mapper
public interface UserAccountMapper {
	int insert(UserAccount dto);
	UserAccount selectForLogin(Map<String,String> map);
	int selectByEmail(String email);	// email 중복검사
	int selectByUserid(String userid);	// userid 중복검사
	int updateEmail(UserAccount dto); // email 수정
	int delete(String userid);			// userid 삭제
	UserAccount selectByPk(String userid); // userid 조회
	List<UserAccount> selectAll (); // 모든 항목을 가져오기때문에 List 사용
}
