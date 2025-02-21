package org.iclass.mvcEx.mapper;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.iclass.mvcEx.dto.UserAccount;

@Mapper
public interface UserAccountMapper {
	int insert(UserAccount dto);
	UserAccount selectForLogin(Map<String,String> map);
	UserAccount insert(Map<String, String> map);
}
