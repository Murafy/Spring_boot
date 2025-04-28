// 채팅 메시지 로깅
package org.iclass.wos.dto;

import java.time.LocalDateTime;

import org.iclass.wos.dto.ChatMessageDto.MessageType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatMessageLoggingDto {
    private Long id;              // 메시지 ID
    private String sender;        // 발신자
    private String content;       // 메시지 내용
    private LocalDateTime timestamp; // 전송 시간
    private MessageType type;     // 메시지 타입
}
