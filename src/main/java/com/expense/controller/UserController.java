package com.expense.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.expense.dto.request.UserRequestDto;
import com.expense.dto.response.UserResponseDto;
import com.expense.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/expense")
public class UserController {

	private UserService userService;
	
	public UserController(UserService userService) {
		this.userService=userService;
	}
	
	@PostMapping("/users")
	public ResponseEntity <UserRequestDto> saveTheUser(@Valid
			@ModelAttribute UserRequestDto user,MultipartFile file)
			throws Exception{
		
		UserRequestDto request=userService.saveUser(user, file);
		return ResponseEntity.ok(request);
	}
	
	@GetMapping("/all")
	public  List <UserResponseDto> getAllUsers(){
      return (List<UserResponseDto>) this.userService.getAllUser();
	}
	
	@GetMapping("/users/{id}")
	public UserResponseDto getAll(@PathVariable int id) {
			return this.userService.getUserById(id);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity <String> deleteId(@Valid @PathVariable int id) {
		try {
		this.userService.deleteById(id);
		return ResponseEntity.ok("Deleted SuccessFully !");
	}catch(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.build();
	}
	}
	
	@PutMapping("/update/{user_id}")
	public ResponseEntity <UserResponseDto> updateUsers(@Valid @RequestBody UserRequestDto user,
			@PathVariable int user_id) {
	UserResponseDto request=userService.updateUser(user, user_id);
	return ResponseEntity.ok(request);
	}
}
