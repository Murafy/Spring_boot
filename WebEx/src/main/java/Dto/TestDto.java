package Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor // 생성자에서 변수값 초기화 (커스텀생성사 생성)
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TestDto {
	private String name; // 객체 기본값 Sring은 null
	private int age;	// double, int 수치형식은 0
	private String address;
	private String gender;
	
}
