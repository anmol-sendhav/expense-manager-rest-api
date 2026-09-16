package com.expense.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.expense.enums.Category;
import com.expense.enums.PaymentMethod;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRequestDto {

	private String title;
	private BigDecimal amount;
	private String description;
	@Enumerated(EnumType.STRING)
	private Category category;
	@Enumerated(EnumType.STRING)
	private LocalDate date;
	private PaymentMethod paymentMethod;
	private Integer userId;
}
