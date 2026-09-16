package com.expense.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter{
	
	@Autowired
	private AuthUtil authUtil;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
			FilterChain filterChain)
			throws ServletException, IOException {
		log.info("Incoming Request {}",request.getRequestURI());
	
	final String requestTokenHeader=request.getHeader("Authorization");
	
	if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
	String token=requestTokenHeader.substring(7);
	//String name=authUtil.getUsernameFromToken(token);
	String email=authUtil.getUserEmailFromToken(token);
	
	if(email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		System.out.println("Reached inside");
		 UserDetails userDetails =
	                userDetailsService.loadUserByUsername(email);
		
		UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken=new 
				UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
	}
	}
	filterChain.doFilter(request, response);
	}
	}
