package com.expense.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {

	private String name;
	private String email;
	private Integer age;
	private String password;
	private String address;
	private String city;
	private String phone;
	private String picture;
}
