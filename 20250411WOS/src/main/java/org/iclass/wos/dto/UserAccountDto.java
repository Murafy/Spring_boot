package org.iclass.wos.dto;

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
@ToString(exclude = "password") // toString 출력시 password 제외
// 로그인 전용 Dto 
public class UserAccountDto {
	private String id; 
	private String password;
	private String nickname;
	private String email;
	private String join_date;
	private String status;
	private String role;
	private String profile_image_url;
	private String address;
	private String phone;
}

