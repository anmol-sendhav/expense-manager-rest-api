package com.expense.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.expense.dto.request.LoginRequestDto;
import com.expense.dto.response.LoginResponseDto;
import com.expense.entity.User;
import com.expense.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

	@Autowired
    private  AuthenticationManager authenticationManager;
	@Autowired
    private  AuthUtil authUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    public String login(LoginRequestDto loginRequestDto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequestDto.getEmail(),
                                loginRequestDto.getPassword()
                        )
                );

        CustomUserDetails customUserDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = customUserDetails.getUser();

        return authUtil.generateAccessToken(user);
    }

    
public LoginResponseDto signUp(LoginRequestDto loginRequestDto) {
		User user = userRepository.findByEmail(
			    loginRequestDto.getEmail()
			).orElse(null);
		
		if(user != null) {
		throw new IllegalArgumentException("User ALready Exist");
		}
			user = userRepository.save(User
					.builder()
					.email(loginRequestDto.getEmail())
					.password(passwordEncoder.encode(loginRequestDto.getPassword()
									)).
					build());
			return new LoginResponseDto(user.getEmail(),
					user.getPassword());
		}
}