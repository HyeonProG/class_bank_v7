package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller // IoC의 대상(싱글톤 패턴으로 관리된다)
@RequestMapping("/user") // 대문 처리(비유적 표현)
public class UserController {

	private UserService userService;
	private final HttpSession session;

	@Autowired // 노란색 경고는 사용할 필요 없음 - 가독성을 위해 선언해도 됨
	public UserController(UserService userService, HttpSession session) {
		this.userService = userService;
		this.session = session;
	}

	/**
	 * 회원 가입 페이지 요청 주소 설계 : http://localhost:8080/user/sign-up
	 * 
	 * @return signUp.jsp
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {

		// prefix : /WEB-INF/view/
		// return : /user/signUp
		// suffix : .jsp
		return "user/signUp";
	}

	/**
	 * 회원 가입 로직 처리 요청 주소 설계 : http://locahost:8080/user/sign-up
	 * 
	 * @param dto
	 * @return
	 */
	@PostMapping("/sign-up")
	public String signUpProc(SignUpDTO dto) {
		System.out.println("test : " + dto.toString());
		// controller 에서 일반적인 코드 작업
		// 1. 인증 검사 (여기서는 인증 검사 불필요)
		// 2. 유효성 검사
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username 을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password 를 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("fullname 을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		// 서비스 객체로 전달
		userService.createUser(dto);

		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 화면 요청 
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * 
	 * @return
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		// 인증 검사, 유효성검사 x
		return "user/signIn";
	}

	/**
	 * 로그인 요청 처리
	 * 주소 설계 : http://localhost:8080/user/sign-in
	 * @return
	 */
	@PostMapping("/sign-in")
	public String signInProc(SignInDTO dto) {
		// 1. 인증검사 x
		// 2. 유효성 검사 o
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password를 입력하세요", HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출
		User principal = userService.readUser(dto);
		
		// 세션 메모리에 등록 처리
		session.setAttribute("principal", principal);
		
		// 새로운 페이지로 이동 처리
		// TODO - 계좌 목록 페이지 이동 처리 예정
		return "redirect:/index";
	}
	
	/**
	 * 로그아웃 기능
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate(); // 로그아웃 됨
		
		return "redirect:/user/sign-in";
	}

}
