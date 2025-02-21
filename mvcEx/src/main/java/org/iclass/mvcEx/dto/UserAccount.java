package org.iclass.mvcEx.dto;

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
public class UserAccount {
	private String userid;
	private String username;
	private String password;
	private String birth;
	private String gender;
	private String email;
}
