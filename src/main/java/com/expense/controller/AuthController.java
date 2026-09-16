package com.expense.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.request.LoginRequestDto;
import com.expense.dto.request.RegisterRequestDto;
import com.expense.security.AuthService;
import com.expense.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private UserService userService;
	
	private AuthService authService;
	
	public AuthController(UserService userService,AuthService service) {
		this.userService=userService;
		this.authService=service;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request){
			return ResponseEntity.ok(this.userService.register(request));
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequestDto
			request){
		try {
			String token=this.authService.login(request);
			return ResponseEntity.ok(token);
		}catch(Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(e.getMessage());
		}
	}
}
