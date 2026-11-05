package com.expense.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.expense.dto.request.ExpenseRequestDto;
import com.expense.dto.response.ExpenseResponseDto;
import com.expense.entity.Expenses;
import com.expense.enums.Category;

public interface ExpenseService {

	public Expenses addExpense(ExpenseRequestDto request, Integer userId);
	List <ExpenseResponseDto> getAllExpenses(Integer userId);
	ExpenseResponseDto getExpensesById(
	        Integer expenseId,
	        Integer userId
	);
public Expenses updateExpense(ExpenseRequestDto request,Integer expenseId,Integer userId);
void deleteExpenseById(Integer expenseId,Integer userId);

	public List<Expenses> searchByCategory(Integer userId,Category category);
	public List<Expenses> searchByDateBetween(
			Integer userId,
			LocalDate from,
			LocalDate to);
	List<Expenses> searchByMonth(
	        Integer userId,
	        LocalDate month);
	public Page<Expenses> getExpenses(
	        int page,
	        int size,
	        Integer userId
	);

}
