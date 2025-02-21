package org.iclass.mvcEx.service;

import java.util.HashMap;
import java.util.Map;

import org.iclass.mvcEx.dto.UserAccount;
import org.iclass.mvcEx.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserAccountService {
	private UserAccountMapper mapper;

	public UserAccount login(String userid, String password) {
		Map<String, String> map = new HashMap<>();
		map.put("userid", userid);
		map.put("password", password);
		return mapper.selectForLogin(map); // 메서드로 map을 넘겨줌
	}

	public int join(UserAccount dto) {
		return mapper.insert(dto);

	}
}