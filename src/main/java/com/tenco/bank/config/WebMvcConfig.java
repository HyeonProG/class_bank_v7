package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthIntercepter;

import lombok.RequiredArgsConstructor;

@Configuration // 여러개의 Bean 등록 가능
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
	
	@Autowired // DI
	private final AuthIntercepter authIntercepter;
	
	// @RequiredArgsConstructor <-- 생성자 대신 사용 가능

	// 우리가 만들어 놓은 AuthInterceptor를 등록해야 한다.
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(authIntercepter)
				.addPathPatterns("/account/**")
				.addPathPatterns("/auth/**");
	}
	
	@Bean // IoC 대상 (싱글톤 처리)
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
