package org.iclass.mybatisEx.mapper;


import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.iclass.mybatisEx.dto.UserAccount;


@Mapper
public interface UserAccountMapper {
	int insert(UserAccount dto);
	UserAccount selectForLogin(Map<String,String> map);
}
