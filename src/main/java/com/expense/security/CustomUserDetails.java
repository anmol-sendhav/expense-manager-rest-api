package com.expense.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.expense.entity.User;

@SuppressWarnings("serial")
public class CustomUserDetails  implements UserDetails{

	private User user;
	
	public CustomUserDetails(User user) {
		this.user=user;
		
	}
	public User getUser() {
        return user;
    }

	@Override
	public  String getPassword() {
	 return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.emptyList();
	}
}
