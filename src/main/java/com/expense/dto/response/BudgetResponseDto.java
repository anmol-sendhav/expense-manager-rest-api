package com.expense.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.expense.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetResponseDto {

	private BigDecimal budgetAmount;
	private Category category;
	 @JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate month;
	private BigDecimal totalSpent;
	private BigDecimal remainingAmount;
	private String description;
}
