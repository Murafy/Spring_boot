package org.iclass.wos.dto;

import java.time.LocalDate;

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
public class PostDto {
	private int idx; 
	private String writer; 
	private String title; 
	private String content; 
	private int readcount;
	private int commentcount;
	private String ip;
	private String lang;
	private String thumbnail;
	@JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
	private LocalDate createat; // 타임리프의 날자표현식을 사용하려면 LocalDate
}
