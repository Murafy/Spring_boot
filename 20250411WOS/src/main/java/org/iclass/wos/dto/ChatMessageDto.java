package org.iclass.wos.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ChatMessageDto {
    private String sender;      // 발신자 (닉네임)
    private String content;     // 메시지 내용
    private LocalDateTime timestamp; // 전송 시간
    private MessageType type;   // 메시지 타입 (입장, 퇴장, 채팅)
    
    // 메시지 타입 열거형
    public enum MessageType {
        CHAT,       // 일반 채팅 메시지
        JOIN,       // 채팅방 입장
        LEAVE       // 채팅방 퇴장
    }
}