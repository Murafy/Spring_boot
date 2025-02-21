package org.iclass.mvcEx.service;

import org.iclass.mvcEx.dto.UserAccount;
import org.iclass.mvcEx.mapper.UserAccountMapper;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class UserRegisterService {
	private UserAccountMapper mapper;
	
	public int register(UserAccount dto) {
			return mapper.insert(dto);
	}
}
