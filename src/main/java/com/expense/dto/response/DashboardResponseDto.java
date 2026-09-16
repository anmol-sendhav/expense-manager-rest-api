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

	
	private BigDecimal totalIncome;
	private BigDecimal totalExpense;
	private BigDecimal balance;
   	private BigDecimal todayExpense;
	private BigDecimal weekExpense;
	private BigDecimal monthExpense;
	private BigDecimal highestExpense;
	private BigDecimal lowestExpense;
}
