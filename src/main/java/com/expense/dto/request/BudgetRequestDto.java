package com.expense.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import com.expense.enums.Category;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetRequestDto {

	@Positive(message="Enter Budget Amount")
	private BigDecimal budgetAmount;
	@NotBlank(message="Must be entered Category")
	private Category category;
	 @JsonFormat(pattern = "dd-MM-yyyy")
	private LocalDate month;
	 @NotBlank(message="Enter the Description First")
	 private String description;
}
