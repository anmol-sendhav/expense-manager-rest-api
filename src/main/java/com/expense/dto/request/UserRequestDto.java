package com.expense.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {

	@NotBlank(message="Invalid name")
	private String name;
	@Email(message="Email is required")
	private String email;
	
	@Positive(message="Age must be positive")
	private Integer age;
	private String address;
	@NotBlank
	private String city;
	@NotBlank(message="Invalid phone number")
	private String phone;
	@NotBlank
	private String password;
	private String picture;
}
