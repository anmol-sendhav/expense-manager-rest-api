package com.expense.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.expense.entity.User;
import com.expense.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{

private UserRepository repo;

public CustomUserDetailsService(UserRepository repo) {
	this.repo=repo;
}

@Override
public UserDetails loadUserByUsername(String name)
        throws UsernameNotFoundException {

    //User user = repo.findByName(name)
    		User user = repo.findByEmail(name)
            .orElseThrow(() ->
                    new UsernameNotFoundException(
                            "User not found: " + name
                    ));

    return new CustomUserDetails(user);
}

}
