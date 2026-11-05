package com.expense.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expense.dto.response.NotificationResponseDto;
import com.expense.entity.Notifications;
import com.expense.entity.User;
import com.expense.repository.NotificationRepository;
import com.expense.repository.UserRepository;
import com.expense.service.NotificationService;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }

    private NotificationResponseDto convertToDto(Notifications notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDto> getMyNotifications(String email) {

        User user = getUser(email);

        return notificationRepository
                .findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {

        User user = getUser(email);

        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Override
    public void markAsRead(Long id, String email) {

        User user = getUser(email);

        Notifications notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    @Override
    public void markAllAsRead(String email) {

        User user = getUser(email);

        List<Notifications> notifications =
                notificationRepository
                        .findAllByUserOrderByCreatedAtDesc(user);

        notifications.forEach(notification ->
                notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    @Override
    public void deleteNotification(Long id, String email) {

        User user = getUser(email);

        Notifications notification = notificationRepository
                .findByIdAndUser(id, user)
                .orElseThrow(() ->
                        new RuntimeException("Notification not found"));

        notificationRepository.delete(notification);
    }

    @Override
    public void createNotification(
            String email,
            String title,
            String message,
            String type) {

        User user = getUser(email);

        Notifications notification =
                new Notifications(title, message, type, user);

        notificationRepository.save(notification);
    }
}