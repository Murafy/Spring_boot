package org.iclass.rest.service;

import java.util.List;

import org.iclass.rest.dto.UserAccount;
import org.iclass.rest.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor // final 변수만 생성자 초기화 (실무.ver)
@Service
@Slf4j
public class UserAccountService {
	private final UserAccountMapper mapper;
	
	// 사용자 등록
	public int regist(UserAccount dto) {
		return mapper.insert(dto);
	}
	// 사용자 이메일 수정
	public int updateEamil(UserAccount dto) {
		return mapper.updateEmail(dto);
	}
	// 사용자 삭제
	public int delete(String userid) {
		return mapper.delete(userid);
	}
	// 사용자 정보 조회
	public UserAccount userinfo(String userid) {
		return mapper.selectByPk(userid);
	}
	// 이메일 사용 가능 여부 (중복검사) - boolean 사용가능하면 true
	public boolean vaildEmail(String email) {
		return (mapper.selectByUserid(email)==0);
	}
	
	// userid 중복검사
	public boolean vaildUserid(String email) {
	return (mapper.selectByUserid(email)==0);
}
	
	// 모든 유저 가져오기
	public List<UserAccount> getList(){
		return (mapper.selectAll());
	}
}