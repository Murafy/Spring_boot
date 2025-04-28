package org.iclass.wos.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LoginActivityDto {
    private Long activityId;
    private String userId;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime loginTime;
    
    private String ipAddress;
    private String userAgent;
    private String loginStatus;  // SUCCESS, FAILURE
    private String loginType;    // NORMAL, REMEMBER_ME
}