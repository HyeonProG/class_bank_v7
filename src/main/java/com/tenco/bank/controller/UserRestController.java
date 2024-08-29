package com.tenco.bank.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tenco.bank.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api-user")
@RequiredArgsConstructor
public class UserRestController {
	// DI 처리
	private final UserService userService;
	
	// 주소
	// localhost:8080/api-user/check-username?username=홍길동
	@GetMapping("/check-username")
	public boolean checkName(@RequestParam(name = "username") String username) {
		boolean isUse = userService.searchUsername(username) == null ? true : false;
		return isUse;
	}
	
}
