package com.expense.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.expense.enums.PaymentWay;

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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Income {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY)
	private Integer userIncomeId;
	private String title;
	private String source;
	@CreatedDate
	@Column(updatable=false)
	private LocalDateTime createdAt;
	@LastModifiedDate
	private LocalDateTime updatedAt;
	private BigDecimal amount;
	private String description;
	private LocalDate amountDate;
	@Enumerated(EnumType.STRING)
	private PaymentWay paymentWay;
	@ManyToOne
	private User user;
}
