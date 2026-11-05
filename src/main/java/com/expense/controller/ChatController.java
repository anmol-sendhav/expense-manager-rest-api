
package com.expense.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.expense.dto.request.ChatRequestDto;
import com.expense.dto.response.ChatResponseDto;
import com.expense.service.ChatService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ResponseEntity<ChatResponseDto> chat(
            @Valid @RequestBody ChatRequestDto request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        String answer = chatService.chat(
                request.getMessage(),
                userEmail
        );

        return ResponseEntity.ok(
                new ChatResponseDto(answer)
        );
    }
}
