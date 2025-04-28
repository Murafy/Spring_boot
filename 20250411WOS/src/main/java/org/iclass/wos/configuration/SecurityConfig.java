package org.iclass.wos.configuration;

import org.iclass.wos.security.jwt.JwtAccessDeniedHandler;
import org.iclass.wos.security.jwt.JwtAuthenticationEntryPoint;
import org.iclass.wos.security.jwt.JwtAuthenticationFilter;
import org.iclass.wos.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    public SecurityConfig(JwtTokenProvider tokenProvider, 
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                        JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF 보호 비활성화
            .csrf(csrf -> csrf.disable())
            // 예외 처리 설정
            .exceptionHandling(exception -> 
                exception
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 처리
                    .accessDeniedHandler(jwtAccessDeniedHandler) // 403 처리
            )
            // 세션 관리 설정 - 상태 없음(stateless)
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // HTTP 요청에 대한 권한 설정
            .authorizeHttpRequests(auth -> auth
                // 인증 없이 접근 가능한 정적 리소스
                .requestMatchers("/","/main", "/assets/**", 
                                "/favicon.ico", "/uploads/**").permitAll()
                // 인증 없이 접근 가능한 API 및 회원가입/로그인
                .requestMatchers("/api/regist/**", "/api/email/**", 
                                "/api/user/login", "/api/user/refresh-token", 
                                "/api/user/status", "/api/auth/check").permitAll()
                // 메인 페이지 관련 경로는 모두에게 허용
                .requestMatchers("/main/regist").permitAll()
                .requestMatchers("/read").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/board/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/read").permitAll()
                // 게시글 조회 로그인해야함
                .requestMatchers("/main/board/**").authenticated()
                // 채팅 페이지는 인증된 사용자만 접근 가능
                .requestMatchers("/chat").authenticated()
                // 어드민 페이지는 ADMIN 권한 필요
                .requestMatchers("/main/admin/**", "/api/admin/**").hasRole("ADMIN")
                // 글쓰기 페이지는 인증된 사용자만 접근 가능
                .requestMatchers("/write").authenticated()
                .requestMatchers("/api/post/save").authenticated()
                // 마이페이지는 인증된 사용자만 접근 가능
                .requestMatchers("/mypage/**", "/api/mypage/**").authenticated()
                // 좋아요 기능은 인증된 사용자만 가능
                .requestMatchers("/api/post/like").authenticated()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );
        
        // JWT 필터 추가
        http.addFilterBefore(
            new JwtAuthenticationFilter(tokenProvider), 
            UsernamePasswordAuthenticationFilter.class
        );
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8090")); // 프론트엔드 URL
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}