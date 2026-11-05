package com.expense.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
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


	final private AuthUtil authUtil;
    final private CustomUserDetailsService userDetailsService;

	@Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();

        // Never parse an old JWT on public authentication endpoints.
        return "/auth/login".equals(path)
                || "/auth/register".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.info("Incoming Request {}", request.getRequestURI());

        String authorizationHeader = request.getHeader("Authorization");

log.info("Request URI: {}", request.getRequestURI());
log.info("Authorization header present: {}",
        authorizationHeader != null
        && authorizationHeader.startsWith("Bearer "));

if (authorizationHeader != null
        && authorizationHeader.startsWith("Bearer ")) {

    String token = authorizationHeader.substring(7).trim();

    if (!token.isEmpty()
            && SecurityContextHolder.getContext()
                    .getAuthentication() == null) {
        try {
            String email = authUtil.getUserEmailFromToken(token);

                    if (email != null) {
                        UserDetails userDetails =
                                userDetailsService.loadUserByUsername(email);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities());

                        SecurityContextHolder.getContext()
                                .setAuthentication(authentication);
                    }

        } catch (ExpiredJwtException ex) {
            log.warn("JWT expired for request {}", request.getRequestURI());
            SecurityContextHolder.clearContext();

        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("Invalid JWT for request {}", request.getRequestURI());
            SecurityContextHolder.clearContext();
        }
            }
        }

        // Continue exactly once. Protected endpoints still require authentication.
        filterChain.doFilter(request, response);
    }

	}
