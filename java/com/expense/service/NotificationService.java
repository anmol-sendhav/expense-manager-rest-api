package com.expense.service;

import java.util.List;

import com.expense.dto.response.NotificationResponseDto;

public interface NotificationService {

    List<NotificationResponseDto> getMyNotifications(String email);

    long getUnreadCount(String email);

    void markAsRead(Long id, String email);

    void markAllAsRead(String email);

    void deleteNotification(Long id, String email);

    void createNotification(
            String email,
            String title,
            String message,
            String type
    );
}