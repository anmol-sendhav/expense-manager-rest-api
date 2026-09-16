package com.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.expense.enums.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Budget {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private BigDecimal budgetAmount;
	@Enumerated(EnumType.STRING)
	private Category category;
    private LocalDate month;
    @CreatedDate
    @Column(updatable=false)
	private LocalDateTime createdAt;
	private String description;
	@ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
	private User user;
	
}
