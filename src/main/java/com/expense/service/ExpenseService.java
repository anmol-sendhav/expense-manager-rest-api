package com.expense.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import com.expense.dto.request.ExpenseRequestDto;
import com.expense.dto.response.ExpenseResponseDto;
import com.expense.entity.Expenses;
import com.expense.enums.Category;

public interface ExpenseService {

	public Expenses addExpense(ExpenseRequestDto request);
	List <ExpenseResponseDto> getAllExpenses();
	ExpenseResponseDto getExpensesById(Integer id);
	public Expenses updateExpense(ExpenseRequestDto expense,int expense_id);
	public void deleteExpenseById(int id);
	public List<Expenses> searchByCategory(Integer userId,Category category);
	public List<Expenses> searchByDateBetween(
			Integer userId,
			LocalDate from,
			LocalDate to);
	List<Expenses> searchByMonth(
	        Integer userId,
	        LocalDate month);
	public Page<Expenses> getExpenses(int page, int size);
	
}
