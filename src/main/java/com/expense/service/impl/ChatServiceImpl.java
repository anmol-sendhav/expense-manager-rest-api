
package com.expense.service.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.expense.service.ChatService;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatClient chatClient;

    public ChatServiceImpl(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                    You are ExpensePilot AI, a helpful personal
                    finance assistant.

                    Your responsibilities:
                    1. Explain personal finance concepts.
                    2. Help users understand budgeting and saving.
                    3. Give practical expense-management suggestions.
                    4. Use simple and professional language.
                    5. Never invent a user's financial information.
                    6. If you do not have financial data, clearly
                       explain that it is not available.
                    7. Do not provide investment or tax guarantees.
                    """)
                .build();
    }

    @Override
    public String chat(String message, String userEmail) {

        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
