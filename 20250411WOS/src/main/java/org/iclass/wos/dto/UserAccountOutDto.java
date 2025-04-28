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
@ToString

// 해당 Dto는 로그인후 마이페이지 용도임
public class UserAccountOutDto {
	private String id;
	private String nickname;
	private String email;
	private String join_date;
	private String role;
	private String profileImageUrl;
	private String address;
	private String phone;
}