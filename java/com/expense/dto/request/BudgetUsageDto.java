package com.expense.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BudgetUsageDto {

	    private Integer budgetId;
	    @Positive(message="Enter Budget Amount First")
	    private BigDecimal budgetAmount;
	    private BigDecimal usedAmount;
	    private BigDecimal remainingAmount;
	    private BigDecimal usagePercentage;
	    private String status;
	}
