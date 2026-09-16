package com.expense.service.impl;

import com.expense.repository.UserRepository;
import com.expense.service.ExpenseService;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.expense.dto.request.ExpenseRequestDto;
import com.expense.dto.response.ExpenseResponseDto;
import com.expense.entity.Expenses;
import com.expense.entity.User;
import com.expense.enums.Category;
import com.expense.exception.ResourceNotFoundException;
import com.expense.repository.ExpenseRepository;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

	private final UserRepository userRepository;
	private final ExpenseRepository expenseRepository;
	
	public Expenses addExpense(ExpenseRequestDto request) {

		User user = userRepository.findById(request.getUserId())
		        .orElseThrow(() -> new ResourceNotFoundException("User Not Found"));
		
		Expenses expense=new Expenses();
		
		expense.setTitle(request.getTitle());
		expense.setCategory(request.getCategory());
		expense.setAmount(request.getAmount());
		expense.setDescription(request.getDescription());
		expense.setPaymentMethod(request.getPaymentMethod());
		expense.setUser(user);
		expense.setDate(LocalDate.now());
		
		return expenseRepository.save(expense);

	}
	
	public List <ExpenseResponseDto> getAllExpenses() {

		List<Expenses> expense=expenseRepository.findAll();
		List<ExpenseResponseDto> responses1=new ArrayList<>();
		
		for(Expenses expenses:expense) {
			ExpenseResponseDto response =new ExpenseResponseDto();
			
			response.setAmount(expenses.getAmount());
			response.setCategory(expenses.getCategory());
			response.setDescription(expenses.getDescription());
			response.setPaymentMethod(expenses.getPaymentMethod());
			response.setTitle(expenses.getTitle());
			response.setUserId(expenses.getExpenseId());
			
			responses1.add(response);
		}
			return responses1;
	}

	@Override
	public ExpenseResponseDto getExpensesById(Integer id) {
		Expenses expense=expenseRepository
				.findById(id)
				.orElseThrow(() ->
				new ResourceNotFoundException("Expenses not found with id"+id));
		
		ExpenseResponseDto response=new ExpenseResponseDto();
		
		response.setAmount(expense.getAmount());
		response.setCategory(expense.getCategory());
		response.setDescription(expense.getDescription());
		response.setPaymentMethod(expense.getPaymentMethod());
		response.setTitle(expense.getTitle());
		response.setUserId(expense.getExpenseId());
		
		return response;
	}
	
	public Expenses updateExpense(ExpenseRequestDto request, int expense_id) {
		Expenses e=expenseRepository.findById(expense_id).orElseThrow(
				() -> new ResourceNotFoundException("Expense not found with id:"+expense_id));
		if(e != null) {
			e.setAmount(request.getAmount());
			e.setCategory(request.getCategory());
			e.setDescription(request.getDescription());
			e.setPaymentMethod(request.getPaymentMethod());
			e.setTitle(request.getTitle());
		}
			return expenseRepository.save(e);
	}

	public void deleteExpenseById(int id) {
		expenseRepository.deleteById(id);;
		
	}

	@Override
	public List<Expenses> searchByCategory(Integer userId, Category category) {
		return expenseRepository
				.findByUserIdAndCategory(userId, category);
	}

	@Override
	public List<Expenses> searchByMonth(
	        Integer userId,
	        LocalDate month) {

	    LocalDate startDate = month.withDayOfMonth(1);
	    LocalDate endDate = startDate.plusMonths(1);

	    return expenseRepository
	            .findByUserIdAndDateGreaterThanEqualAndDateLessThan(
	                    userId,
	                    startDate,
	                    endDate);
	}
@Override
	public List<Expenses> searchByDateBetween(Integer userId,
			LocalDate from,
			LocalDate to) {
		return expenseRepository
				.findByUserIdAndDateGreaterThanEqualAndDateLessThan(userId,from,to);
	}

public Page<Expenses> getExpenses(int page, int size){

    Pageable pageable =PageRequest.of(page,size);
    return expenseRepository.findAll(pageable);
}
}
