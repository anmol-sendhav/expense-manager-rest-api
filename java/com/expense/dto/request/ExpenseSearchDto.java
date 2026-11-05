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
public class ExpenseSearchDto {

	@NotBlank(message="Please Enter at least 1 Category")
	private Category category;
	@JsonFormat(pattern="dd-MM-yy")
	@NotBlank(message="Enter Valid Date")
	private LocalDate fromDate;
	@JsonFormat(pattern="dd-MM-yy")
	private LocalDate toDate;
	@Positive(message="Please Enter minimum Amount")
	private BigDecimal minAmount;
	@Positive(message="Enter maximum Amount")
	private BigDecimal maxAmount;
	@NotBlank(message="Must enter the description")
	private String description;
}
