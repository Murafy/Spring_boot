package org.iclass.wos.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@AllArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;

    /*
    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    */
    
    public void sendVerificationEmail(String to, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("이메일 인증 코드");
        message.setText("회원가입을 위한 인증 코드입니다: " + verificationCode + "\n\n"
                + "이 코드는 5분 동안 유효합니다.");
        
        log.info("이메일 발송 확인 {}",message);
        mailSender.send(message);
    }
}