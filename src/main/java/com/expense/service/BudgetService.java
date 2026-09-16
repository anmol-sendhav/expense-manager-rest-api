package com.expense.service;

import java.util.List;

import com.expense.dto.request.BudgetRequestDto;
import com.expense.dto.request.BudgetUsageDto;
import com.expense.dto.response.BudgetResponseDto;

public interface BudgetService {

	public BudgetResponseDto saveBudget(BudgetRequestDto budget);
	List <BudgetResponseDto> getAllBudgetsForUser();
	public BudgetResponseDto getBudgetById(int id);
	public BudgetResponseDto updateBudget(BudgetRequestDto request,int id);
	public BudgetUsageDto getBudgetUsage(int budgetID);
	void deleteBudgetById(int id);
}
