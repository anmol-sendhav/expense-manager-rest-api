package com.expense.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.expense.dto.request.LoginRequestDto;
import com.expense.dto.request.RegisterRequestDto;
import com.expense.dto.request.UserRequestDto;
import com.expense.dto.response.UserResponseDto;
import com.expense.entity.User;
import com.expense.exception.ResourceNotFoundException;
import com.expense.repository.UserRepository;
import com.expense.security.AuthUtil;
import com.expense.security.CustomUserDetails;
import com.expense.service.UserService;

@Service
public class UserServiceImpl implements UserService{

	private final UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	
	private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
	
	public UserServiceImpl(UserRepository userRepository,PasswordEncoder passwordEncoder,
	AuthenticationManager authenticationManager,AuthUtil authUtil)
	{
		this.userRepository=userRepository;
		this.passwordEncoder=passwordEncoder;
		this.authenticationManager = authenticationManager;
        this.authUtil = authUtil;
	}
	
	/*
	public User saveUser(UserRequestDto request){
		    User user = new User();
		    
		    user.setName(request.getName());
		    user.setEmail(request.getEmail());
		    user.setAge(request.getAge());
		    user.setPassword(passwordEncoder.encode(request.getPassword()));
		    user.setAddress(request.getAddress());
		    user.setCity(request.getCity());
		    user.setPhone(request.getPhone());
		    user.setPicture(request.getPicture());
		    return userRepository.save(user);
		}
	*/
	
		
	public UserRequestDto saveUser(UserRequestDto dto,MultipartFile file)throws IOException {

	    User user = new User();
	    user.setName(dto.getName());
	    user.setEmail(dto.getEmail());
	    user.setAge(dto.getAge());
	    user.setPassword(passwordEncoder.encode(dto.getPassword()));
	    user.setAddress(dto.getAddress());
	    user.setCity(dto.getCity());
	    user.setPhone(dto.getPhone());
	    
	    if (file != null && !file.isEmpty()) {
	    	
	        String fileName =UUID.randomUUID()+ "_"+ file.getOriginalFilename();
	        Path path = Paths.get("D:\\My Movies\\College").resolve(fileName);
	        Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
	        user.setPicture("profile/" + fileName);    
	    }
	    User savedUser = userRepository.save(user);

	    UserRequestDto response = new UserRequestDto();
	    response.setName(savedUser.getName());
	    response.setEmail(savedUser.getEmail());
	    response.setAge(savedUser.getAge());
	    response.setAddress(savedUser.getAddress());
	    response.setCity(savedUser.getCity());
	    response.setPhone(savedUser.getPhone());

	    return response;
	}
	
	public List<UserResponseDto> getAllUser(){
		List<User> user=(List<User>) userRepository.findAll();
		List<UserResponseDto> responses=new ArrayList<>();
		
		for(User users:user) {
			UserResponseDto response =new UserResponseDto();
			response.setAddress(users.getAddress());
			response.setAge(users.getAge());
			response.setCity(users.getCity());
			response.setEmail(users.getEmail());
			response.setName(users.getName());
			response.setPhone(users.getPhone());
			response.setPicture(users.getPicture());
			
			 responses.add(response);
		}
		return responses;
	}
	
	public UserResponseDto getUserById(int id) {
		User user=userRepository.findById(id).orElseThrow(() ->
		new ResourceNotFoundException("User not found with"+id));
		
		UserResponseDto response=new UserResponseDto();
		response.setAddress(user.getAddress());
		response.setAge(user.getAge());
		response.setCity(user.getCity());
		response.setEmail(user.getEmail());
		response.setName(user.getName());
		response.setPhone(user.getPhone());
		response.setPicture(user.getPicture());
		
		return response;
	}
	
	public UserResponseDto updateUser(UserRequestDto user,int id) {
		User u=userRepository.findById(id)
				.orElseThrow(
				() -> new ResourceNotFoundException("User not found"));
	
			u.setName(user.getName());
			u.setAddress(user.getAddress());
			u.setAge(user.getAge());
			u.setCity(user.getCity());
			u.setEmail(user.getEmail());
			u.setPhone(user.getPhone());
			u.setPicture(user.getPicture());
			
			User saved= userRepository.save(u);
			
			UserResponseDto response=new UserResponseDto();
			response.setAddress(saved.getAddress());
			response.setAge(saved.getAge());
			response.setCity(saved.getCity());
			response.setEmail(saved.getEmail());
			response.setName(saved.getName());
			response.setPhone(saved.getPhone());
			response.setPicture(saved.getPicture());
			
			return response;
	}
	
	public void deleteById(int id) {
		userRepository.deleteById(id);
	}	
	
	public User register(RegisterRequestDto request) {
		User user=new User();
		
		user.setName(request.getUsername());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setEmail(request.getEmail());
		
		return userRepository.save(user);
		
	}
	@Override
	public String login(LoginRequestDto request) {

	    Authentication authentication =
	            authenticationManager.authenticate(
	                    new UsernamePasswordAuthenticationToken(
	                            request.getEmail(),
	                            request.getPassword()
	                    )
	            );

	    Object principal = authentication.getPrincipal();

	    System.out.println("Principal = " + principal.getClass());

	    CustomUserDetails details =
	            (CustomUserDetails) principal;

	    User user = details.getUser();

	    return authUtil.generateAccessToken(user);
	}
}