package org.iclass.wos.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.iclass.wos.dto.RefreshTokenDto;

@Mapper
public interface RefreshTokenMapper {
    int saveToken(RefreshTokenDto token);
    RefreshTokenDto findByToken(String token);
    RefreshTokenDto findByUserId(String userId);
    void deleteByUserId(String userId);
}