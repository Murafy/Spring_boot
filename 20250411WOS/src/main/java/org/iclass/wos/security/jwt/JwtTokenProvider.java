package org.iclass.wos.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
public class JwtTokenProvider {
    
    private final Key key;
    private final JwtConfig jwtConfig;
    
    public JwtTokenProvider(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
    }
    
    // Access Token 생성
    public String createAccessToken(String userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getAccessTokenExpirationMs());
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("role", role)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    // Refresh Token 생성
    public String createRefreshToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getRefreshTokenExpirationMs());
        
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }
    
    // 토큰 유효성 검증 메소드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    // 토큰에서 사용자 ID 추출
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
    
    // 인증 객체 생성
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            String role = claims.get("role", String.class);
            
            return new UsernamePasswordAuthenticationToken(
                    claims.getSubject(), 
                    null, 
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );
        } catch (Exception e) {
            return null;
        }
    }
    
    // 액세스 토큰 쿠키 생성 (HttpOnly: false)
    public ResponseCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from("accessToken", token)
                .httpOnly(false) // JavaScript에서 접근 가능하도록 설정
                .secure(false) // HTTPS에서는 true로 설정
                .path("/")
                .maxAge(jwtConfig.getAccessTokenExpirationMs() / 1000) // 초 단위로 변환
                .sameSite("Lax")
                .build();
    }
    
    // 리프레시 토큰 쿠키 생성 (HttpOnly: true)
    public ResponseCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true) // JavaScript에서 접근 불가능
                .secure(false) // HTTPS에서는 true로 설정
                .path("/")
                .maxAge(jwtConfig.getRefreshTokenExpirationMs() / 1000) // 초 단위로 변환
                .sameSite("Lax")
                .build();
    }
    
    // 쿠키 삭제를 위한 빈 쿠키 생성
    public ResponseCookie createEmptyAccessTokenCookie() {
        return ResponseCookie.from("accessToken", "")
                .httpOnly(false)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
    }
    
    public ResponseCookie createEmptyRefreshTokenCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .build();
    }
    
    // Refresh Token 만료 시간(밀리초) 반환
    public long getRefreshTokenExpirationMs() {
        return jwtConfig.getRefreshTokenExpirationMs();
    }
    
    // Access Token 만료 시간(밀리초) 반환
    public long getAccessTokenExpirationMs() {
        return jwtConfig.getAccessTokenExpirationMs();
    }
}