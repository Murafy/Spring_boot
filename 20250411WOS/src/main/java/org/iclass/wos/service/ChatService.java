package org.iclass.wos.service;

import java.util.List;

import org.iclass.wos.dto.ChatMessageDto;
import org.iclass.wos.dto.ChatMessageLoggingDto;
import org.iclass.wos.mapper.ChatMessageLoggingMapper;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatMessageLoggingMapper ChatMessageLoggingMapper;
    
    // 메시지 저장
    public void saveMessage(ChatMessageDto message) {
        // ChatMessage Dto 를 LoggingDto로 변환
    	ChatMessageLoggingDto entity = ChatMessageLoggingDto.builder()
                .sender(message.getSender())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .type(message.getType())
                .build();
        
        // DB에 저장
        try {
        	ChatMessageLoggingMapper.insertMessage(entity);
            log.info("채팅 메시지 저장 성공: {}", entity);
        } catch (Exception e) {
            log.error("채팅 메시지 저장 실패: {}", e.getMessage(), e);
        }
    }
    
    // 최근 메시지 조회
    public List<ChatMessageLoggingDto> getRecentMessages(int limit) {
        try {
            // 기본값은 50
            if (limit <= 0) {
                limit = 50;
            }
            return ChatMessageLoggingMapper.getRecentMessages(limit);
        } catch (Exception e) {
            log.error("최근 메시지 조회 실패: {}", e.getMessage(), e);
            return List.of(); // 빈 리스트 반환
        }
    }
}