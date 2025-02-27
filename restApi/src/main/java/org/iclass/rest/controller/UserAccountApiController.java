package org.iclass.rest.controller;

import org.iclass.rest.dto.UserAccount;
import org.iclass.rest.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor // final 변수만 생성자 초기화 (실무.ver)
public class UserAccountApiController {
	private final UserAccountService service;

	// userid로 계정 정보 끌어오기
	@GetMapping("/account/{userid}")
	public ResponseEntity<UserAccount> getOne(@PathVariable String userid) {
		UserAccount account = service.userinfo(userid);
		log.info("유저찾기 : {}", account);
		return ResponseEntity.ok().body(account);
	}

	// web 요청에 따라 userid 추가
	@PostMapping("/account")
	public ResponseEntity<?> newUser(@RequestBody UserAccount dto) {
		try {
			log.info("추가할 유저 : {}", dto);
			int account = service.regist(dto);
			return ResponseEntity.ok().body(account);
		} catch (Exception e) {
			log.info("user 추가 오류 : {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	// web 요청에 따라 특정 유저 email 변경
	@PatchMapping("/account")
	public ResponseEntity<?> soojung(@RequestBody UserAccount dto) {
		try {
			log.info("이메일 변경할 유저 :{},{}", dto);
			int account = service.updateEamil(dto);
			return ResponseEntity.ok().body(account);
		} catch (Exception e) {
			log.info("이메일 변경 오류 : {}", e.getMessage());
			return ResponseEntity.badRequest().build();
		}

	}

	@DeleteMapping("account/{userid}")
	public ResponseEntity<?> delete(@PathVariable String userid) {
		try {
			log.info("삭제 타켓 {}: ", userid);
			int account = service.delete(userid);
			return ResponseEntity.ok().body(account);
		} catch (Exception e) {
			log.info("타켓 도주 : ", e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

}
