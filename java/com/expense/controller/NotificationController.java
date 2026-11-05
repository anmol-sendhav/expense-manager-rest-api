package com.expense.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.expense.dto.response.NotificationResponseDto;
import com.expense.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            Principal principal) {

        return ResponseEntity.ok(
                notificationService.getMyNotifications(principal.getName())
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(
            Principal principal) {

        long count =
                notificationService.getUnreadCount(principal.getName());

        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(
            @PathVariable Long id,
            Principal principal) {

        notificationService.markAsRead(id, principal.getName());

        return ResponseEntity.ok(
                Map.of("message", "Notification marked as read")
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(
            Principal principal) {

        notificationService.markAllAsRead(principal.getName());

        return ResponseEntity.ok(
                Map.of("message", "All notifications marked as read")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteNotification(
            @PathVariable Long id,
            Principal principal) {

        notificationService.deleteNotification(id, principal.getName());

        return ResponseEntity.ok(
                Map.of("message", "Notification deleted successfully")
        );
    }
}