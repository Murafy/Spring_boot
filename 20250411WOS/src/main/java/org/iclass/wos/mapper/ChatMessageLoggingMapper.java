package org.iclass.wos.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.iclass.wos.dto.ChatMessageLoggingDto;

@Mapper
public interface ChatMessageLoggingMapper {
	 // 메시지 저장
    void insertMessage(ChatMessageLoggingDto message);
    
    // 최근 메시지 조회 (최대 50개)
    List<ChatMessageLoggingDto> getRecentMessages(@Param("limit") int limit);
}
