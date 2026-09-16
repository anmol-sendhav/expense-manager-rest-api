package com.expense.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.expense.enums.PaymentWay;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class IncomeRequestDto {

	@NotNull(message="Title is required")
	private String title;
	@NotBlank(message="Source is required")
	private String source;
	@Enumerated(EnumType.STRING)
	@NotBlank(message="PaymentWay required")
	private PaymentWay paymentWay;
	@Positive(message="Amount must be positive")
	@NotBlank
	private BigDecimal amount;
	@NotBlank(message="description is required")
	private String description;
	private Integer userId;
	@NotBlank
	private LocalDate amountDate;
}
