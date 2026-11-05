package com.expense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.request.ProfileUpdateRequestDto;
import com.expense.dto.response.ProfileResponseDto;
import com.expense.entity.User;
import com.expense.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ProfileResponseDto> getMyProfile(
            Authentication authentication) {

        User user = getAuthenticatedUser(authentication.getName());

        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping
    public ResponseEntity<ProfileResponseDto> updateMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileUpdateRequestDto request) {

        User user = getAuthenticatedUser(authentication.getName());

        user.setName(request.getName().trim());
        user.setAge(request.getAge());
        user.setPhone(request.getPhone().trim());
        user.setAddress(request.getAddress());
        user.setCity(request.getCity().trim());

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(toResponse(savedUser));
    }

    private User getAuthenticatedUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Authenticated user not found"));
    }

    private ProfileResponseDto toResponse(User user) {

        ProfileResponseDto response = new ProfileResponseDto();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setPhone(user.getPhone());
        response.setAddress(user.getAddress());
        response.setCity(user.getCity());
        response.setPicture(user.getPicture());

        return response;
    }
}