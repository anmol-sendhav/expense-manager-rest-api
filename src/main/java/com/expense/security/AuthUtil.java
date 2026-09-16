package com.expense.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.expense.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class AuthUtil {

	@Value("${SECRET_KEY}")
	private String SECRET_KEY;
	
	private SecretKey getSecretKey() {
		return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
	}
	
	public String generateAccessToken(User user) {
		return Jwts.builder()
				//.setSubject(user.getName())
				.setSubject(user.getEmail())
				.claim("userId", user.getId().toString())
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+
						1000*60*10*10))
				.signWith(getSecretKey())
				.compact();
	}
	
	public String getUserEmailFromToken(String token) {

	    return Jwts.parserBuilder()
	            .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
	            .build()
	            .parseClaimsJws(token)
	            .getBody()
	            .getSubject();
	}
}
