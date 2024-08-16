package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class User {

	private Integer id;
	private String username;
	private String password;
	private String fullname;
	private String originFileName;
	private String uploadFileName;
	private Timestamp createdAt;
	private boolean socialLogin;
	
	public String setUpUserImage() {
		if (socialLogin == true) {
			return originFileName;
		} else {
			return uploadFileName == null ? "https://picsum.photos/id/1/350" : "/images/uploads/" + uploadFileName;
		}
		
	}
	
}
