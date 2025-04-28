package org.iclass.wos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
@ToString(exclude = "password") // toString에서 password 제외
public class UserRegisterDto {
	@NotBlank(message = "아이디는 필수 입력 항목입니다.")
	@Size(min = 4, max = 20, message = "아이디는 4~20자 사이여야합니다.")
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "아이디는 영문자와 숫자만 사용 가능합니다.")
	private String id;
	
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$", 
             message = "비밀번호는 영문자, 숫자, 특수문자를 포함해야 합니다.")
	private String password;
    
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2~10자 사이여야 합니다.")
	private String nickname;
    
    @NotBlank(message = "주소는 필수 입력 항목입니다.")
	private String address;
    
    @Pattern(regexp = "^$|^\\d{11}$" ) // 전화 번호는 11자리만 허용함 (- 하이픈 허용 안함)
	private String phone;
	
	@NotBlank(message = "이메일은 필수 입력 항목입니다.")
	private String email;

	// 비밀번호 확인 필드 추가 가능 (서버 측 검증용)
	private String confirmPassword;

}
