package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignInDTO;
import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import lombok.RequiredArgsConstructor;

@Service // IoC 대상(싱글톤으로 관리) - 제어의 역전
@RequiredArgsConstructor
public class UserService {

	@Autowired // 가독성을 위해 작성
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	/**
	 * 회원 등록 서비스 기능 트랜잭션 처리
	 * 
	 * @param dto
	 */

	@Transactional // 트랜잭션 처리는 반드시 습관화
	public void createUser(SignUpDTO dto) {
		int result = 0;

		try {
			// 코드 추가 부분
			// 회원 가입 요청시 사용자가 던진 비밀번호 값을 암호화 처리해야한다.
			String hashPwd = passwordEncoder.encode(dto.getPassword());
			System.out.println("hashPwd : " + hashPwd);
			dto.setPassword(hashPwd);
			
			result = userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("중복 이름을 사용할 수 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}

		if (result != 1) {
			throw new DataDeliveryException("회원가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public User readUser(SignInDTO dto) {
		// 유효성 검사는 Controller에서 먼저 하자.
		User userEntity = null; // 지역 변수 선언
		
		// 기능 수정
		// username으로만 select 처리
		// 2가지의 경우의 수 --> 객체가 존재, null
		
		// 객체 안에 사용자의 password 가 존재(암호화 되어있는 값)
		// passwordEncorder 안에 matches 메서드를 사용해서 판별한다.
		
		try {
			userEntity = userRepository.findByUsername(dto.getUsername());
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리 입니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}

		if (userEntity == null) {
			throw new DataDeliveryException("존재하지 않는 아이디 입니다.", HttpStatus.BAD_REQUEST);
		}
		
		boolean isPwdMatched = passwordEncoder.matches(dto.getPassword(), userEntity.getPassword());
		if (isPwdMatched == false) {
			throw new DataDeliveryException("비밀번호가 잘못되었습니다.", HttpStatus.BAD_REQUEST);
		}

		return userEntity;
	}

}
