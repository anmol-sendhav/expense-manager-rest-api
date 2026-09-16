package com.expense.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.expense.dto.request.LoginRequestDto;
import com.expense.dto.request.RegisterRequestDto;
import com.expense.dto.request.UserRequestDto;
import com.expense.dto.response.UserResponseDto;
import com.expense.entity.User;

@Service
public interface UserService {

	public UserRequestDto saveUser(UserRequestDto user,MultipartFile file)throws Exception;
	List<UserResponseDto> getAllUser();
	UserResponseDto getUserById(int id);
	public UserResponseDto updateUser(UserRequestDto user,int user_id);
	public void deleteById(int id);
	//public void updateUser(UserRequestDto user, int user_id);
	public User register(RegisterRequestDto request);
	public String login(LoginRequestDto request);
}
