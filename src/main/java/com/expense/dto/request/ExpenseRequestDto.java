package com.expense.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expense.enums.Category;
import com.expense.enums.PaymentMethod;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDto {

	@NotBlank
	private String title;
	@NotNull
	@Positive(message="must be enter the amount")
	private BigDecimal amount;
	@NotBlank(message="Enter the Description")
	private String description;
	@Enumerated(EnumType.STRING)
	private Category category;
	private LocalDate month;
	@Enumerated(EnumType.STRING)
	@NotNull
	private PaymentMethod paymentMethod;
	private Integer userId;
}
