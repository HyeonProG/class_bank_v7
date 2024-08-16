package com.tenco.bank.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;
import lombok.ToString;

// Json 형식의 코딩 컨벤션이 스네이크 케이스를 카멜 노테이션으로 할당한다.
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@Data
@ToString
public class OAuthToken {

	private String accessToken;

	private String tokenType;

	private String refreshToken;

	private Integer expiresIn;

	private String scope;

	private Integer refreshTokenExpiresIn;

}