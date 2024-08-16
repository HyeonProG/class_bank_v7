package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.tenco.bank.dto.KakaoProfile;
import com.tenco.bank.dto.OAuthToken;
import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@Controller // IoC의 대상(싱글톤 패턴으로 관리된다)
@RequestMapping("/user") // 대문 처리(비유적 표현)
@RequiredArgsConstructor
public class UserController {

	@Autowired
	private UserService userService;
	private final HttpSession session;
	
	@Value("${tenco.key}")
	private String tencoKey;


	/**
	 * 회원 가입 페이지 요청 주소 설계 : http://localhost:8080/user/sign-up
	 * 
	 * @return signUp.jsp
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {

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
		// controller 에서 일반적인 코드 작업
		// 1. 인증 검사 (여기서는 인증 검사 불필요)
		// 2. 유효성 검사
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_FULLNAME, HttpStatus.BAD_REQUEST);
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
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		
		// 서비스 호출
		User principal = userService.readUser(dto);
		
		// 세션 메모리에 등록 처리
		session.setAttribute(Define.PRINCIPAL, principal);
		
		// 새로운 페이지로 이동 처리
		return "redirect:/account/list";
	}
	
	/**
	 * 로그아웃 처리
	 * @return
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate(); // 로그아웃 됨
		
		return "redirect:/user/sign-in";
	}
	
	// 소셜 로그인
	@GetMapping("/kakao")
	// @ResponseBody // @RestController = @Controller + @ResponseBody
	public String kakaoCallback(@RequestParam(name = "code", required = false) String code, @RequestParam(name = "error", required = false) String error) {
		System.out.println("code : "+ code);
		
		// POST - 카카오 토큰 요청 받기
		// header, body 구성
		RestTemplate rt1 = new RestTemplate();

		// 헤더 구성
		HttpHeaders header1 = new HttpHeaders();
		header1.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		
		// 바디 구성
		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", "c416e84616208b50d0318d27f1301c29");
		params.add("redirect_uri", "http://localhost:8080/user/kakao");
		params.add("code", code);
		
		// 헤더 + 바디 결합
		HttpEntity<MultiValueMap<String, String>> reqKakaoMessage
			= new HttpEntity<>(params, header1);
		
		// 통신 요청
		ResponseEntity<OAuthToken> response1 = rt1.exchange("https://kauth.kakao.com/oauth/token", 
				HttpMethod.POST, reqKakaoMessage, OAuthToken.class);

		System.out.println("response : " + response1.getBody().toString());
		
		// 카카오 리소스서버 사용자 정보 가져오기
		RestTemplate rt2 = new RestTemplate();
		
		// 헤더
		HttpHeaders header2 = new HttpHeaders();
		// 반드시 Bearer 다음 공백 한칸 추가
		header2.add("Authorization", "Bearer " + response1.getBody().getAccessToken());
		header2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// 본문 x
		
		// HTTP Entity 만들기
		HttpEntity<MultiValueMap<String, String>> reqKakaoInfoMessage = new HttpEntity<>(header2);
		
		// 통신 요청
		ResponseEntity<KakaoProfile> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", 
				HttpMethod.POST, reqKakaoInfoMessage, KakaoProfile.class);
		
		KakaoProfile kakaoProfile = response2.getBody();
		// 카카오 사용자 정보 응답 완료
		System.out.println("kakaoProfile : " + kakaoProfile);
		
		// 최초 사용자라면 자동 회원가입 처리(우리 서버)
		// 회원가입 이력이 있는 사용자라면 바로 세션 처리(우리 서버)
		// 사전 기반 지식 --> 소셜 사용자는 비밀번호를 입력하는가?
		// 우리 서버에 회원가입시 --> password --> not null (무조건 만들어서 넣어야 함 / DB 정책)
		
		// 1. 회원가입 데이터 생성
		SignUpDTO signUpDTO = SignUpDTO.builder()
			.username(kakaoProfile.getProperties().getNickname() + "_" + kakaoProfile.getId())
			.fullname("OAuth_" + kakaoProfile.getProperties().getNickname())
			.password(tencoKey)
			.build();
		
		// 2. 우리 사이트 최초 소셜 사용자 인지 판별
		User oldUser = userService.searchUsername(signUpDTO.getUsername());
		if (oldUser == null) {
			// 사용자가 최초 소셜 로그인
			oldUser = new User();
			oldUser.setUsername(signUpDTO.getUsername());
			oldUser.setPassword(null);
			oldUser.setFullname(signUpDTO.getFullname());

			// 사용자 프로필 이미지 여부에 따라 조건식 추가
			signUpDTO.setOriginFileName(kakaoProfile.getProperties().getThumbnailImage());
			userService.createUser(signUpDTO);
		} else {
			oldUser.setSocialLogin(true);
			oldUser.setOriginFileName(kakaoProfile.getProperties().getThumbnailImage());
		}
		
		// 자동 로그인 처리
		session.setAttribute(Define.PRINCIPAL, oldUser);
		
		return "redirect:/account/list";

	}
	
}
