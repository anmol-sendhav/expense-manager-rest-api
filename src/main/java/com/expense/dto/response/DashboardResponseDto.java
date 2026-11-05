package com.expense.dto.response;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDto {

	    private String userName;
	    private BigDecimal totalIncome;
	    private BigDecimal totalExpenses;
	    private BigDecimal balance;
	    private BigDecimal savingsRate;
	    private long incomeCount;
	    private long expenseCount;
}
