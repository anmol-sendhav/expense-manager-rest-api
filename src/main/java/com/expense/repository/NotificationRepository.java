package com.expense.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.expense.entity.Notifications;
import com.expense.entity.User;

public interface NotificationRepository
        extends JpaRepository<Notifications, Long> {

    List<Notifications> findAllByUserOrderByCreatedAtDesc(User user);

    Optional<Notifications> findByIdAndUser(Long id, User user);

    long countByUserAndReadFalse(User user);
}