package org.iclass.wos.security.jwt;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@Getter
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpirationMs;
    
    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;
    
}