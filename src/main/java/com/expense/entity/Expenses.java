package com.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.expense.enums.Category;
import com.expense.enums.PaymentMethod;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Expenses {

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer expenseId;
	private String title;
	private BigDecimal amount;
	private String description;
	@Column(name = "expense_date")
	private LocalDate date;
	@ManyToOne
	private User user;
	@CreatedDate
	@Column(updatable=false)
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
	@Enumerated(EnumType.STRING)
	private Category category;
	@Enumerated(EnumType.STRING)
	private PaymentMethod paymentMethod;
}
