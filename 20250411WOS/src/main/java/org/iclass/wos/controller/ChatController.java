package org.iclass.wos.controller;

import java.time.LocalDateTime;

import org.iclass.wos.dto.ChatMessageDto;
import org.iclass.wos.exception.BusinessException;
import org.iclass.wos.exception.ErrorCode;
import org.iclass.wos.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    // 웹소켓을 통해 메시지 수신 및 브로드캐스트
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessageDto sendMessage(ChatMessageDto chatMessage) {
        // 메시지 유효성 검사
        if (chatMessage.getSender() == null || chatMessage.getSender().trim().isEmpty()) {
            throw new BusinessException("발신자 정보가 없습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        if (chatMessage.getContent() == null || chatMessage.getContent().trim().isEmpty()) {
            throw new BusinessException("메시지 내용이 없습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        // 타임스탬프 설정
        chatMessage.setTimestamp(LocalDateTime.now());
        log.info("메시지 전송: {}", chatMessage);
        
        try {
            // 메시지 로깅 (저장)
            chatService.saveMessage(chatMessage);
        } catch (Exception e) {
            log.warn("메시지 저장 실패: {}", e.getMessage());
            // 메시지 저장 실패는 채팅 전송을 방해하지 않도록 함
        }
        
        return chatMessage;
    }

    // 사용자 입장 처리
    @MessageMapping("/chat.join")
    @SendTo("/topic/public")
    public ChatMessageDto joinChat(ChatMessageDto chatMessage) {
        if (chatMessage.getSender() == null || chatMessage.getSender().trim().isEmpty()) {
            throw new BusinessException("사용자 정보가 없습니다.", ErrorCode.INVALID_REQUEST);
        }
        
        chatMessage.setType(ChatMessageDto.MessageType.JOIN);
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setContent(chatMessage.getSender() + "님이 채팅방에 참여하셨습니다.");
        log.info("사용자 입장: {}", chatMessage.getSender());
        
        try {
            // 입장 메시지도 저장
            chatService.saveMessage(chatMessage);
        } catch (Exception e) {
            log.warn("입장 메시지 저장 실패: {}", e.getMessage());
        }
        
        return chatMessage;
    }
    
    // 채팅 페이지 접근을 위한 GET 매핑
    @GetMapping("/chat")
    public String getChatPage() {
        return "chat";
    }
}