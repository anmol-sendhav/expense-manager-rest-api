package com.expense.service.impl;

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
import com.expense.repository.UserRepository;
import com.expense.service.ExpenseService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

	private final UserRepository userRepository;
	private final ExpenseRepository expenseRepository;


	@Override
	public ExpenseResponseDto addExpense(ExpenseRequestDto request, Integer userId) {

	    User user = userRepository.findById(userId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("User Not Found"));

	    Expenses expense = new Expenses();

	    expense.setTitle(request.getTitle());
	    expense.setCategory(request.getCategory());
	    expense.setAmount(request.getAmount());
	    expense.setDescription(request.getDescription());
	    expense.setPaymentMethod(request.getPaymentMethod());
	    expense.setUser(user);
	    expense.setDate(LocalDate.now());

	    // Save the expense first
	    Expenses savedExpense = expenseRepository.save(expense);

	    ExpenseResponseDto response = new ExpenseResponseDto();

	    response.setExpenseId(savedExpense.getExpenseId());
	    response.setTitle(savedExpense.getTitle());
	    response.setAmount(savedExpense.getAmount());
	    response.setDescription(savedExpense.getDescription());
	    response.setCategory(savedExpense.getCategory());
	    response.setPaymentMethod(savedExpense.getPaymentMethod());
	    response.setDate(savedExpense.getDate());

	    return response;
	}



@Override
public List<ExpenseResponseDto> getAllExpenses(Integer userId) {

    List<Expenses> expenses = expenseRepository.findByUser_Id(userId);
    List<ExpenseResponseDto> responses = new ArrayList<>();

    for (Expenses expense : expenses) {
        ExpenseResponseDto response = new ExpenseResponseDto();

        response.setExpenseId(expense.getExpenseId());
        response.setTitle(expense.getTitle());
        response.setAmount(expense.getAmount());
        response.setDescription(expense.getDescription());
        response.setCategory(expense.getCategory());
        response.setPaymentMethod(expense.getPaymentMethod());
        response.setDate(expense.getDate());

        responses.add(response);
    }

    return responses;
}

	@Override
	public ExpenseResponseDto getExpensesById(
	        Integer expenseId,
	        Integer userId) {

	    Expenses expense = expenseRepository
	            .findByExpenseIdAndUser_Id(expenseId, userId)
	            .orElseThrow(() ->
	                    new RuntimeException("Expense not found"));

	    ExpenseResponseDto response = new ExpenseResponseDto();

	    response.setExpenseId(expense.getExpenseId());
	    response.setTitle(expense.getTitle());
	    response.setAmount(expense.getAmount());
	    response.setDescription(expense.getDescription());
	    response.setDate(LocalDate.now());
	    response.setCategory(expense.getCategory());
	    response.setPaymentMethod(expense.getPaymentMethod());

	    return response;
	}
	
	@Override
	public ExpenseResponseDto updateExpense(
	        ExpenseRequestDto request,
	        Integer expenseId,
	        Integer userId) {

	    Expenses expense = expenseRepository
	            .findByExpenseIdAndUser_Id(expenseId, userId)
	            .orElseThrow(() ->
	                    new ResourceNotFoundException("Expense not found"));

	    expense.setAmount(request.getAmount());
	    expense.setCategory(request.getCategory());
	    expense.setDescription(request.getDescription());
	    expense.setPaymentMethod(request.getPaymentMethod());
	    expense.setTitle(request.getTitle());

	    Expenses updatedExpense = expenseRepository.save(expense);

	    // Convert entity into DTO
	    ExpenseResponseDto response = new ExpenseResponseDto();

	    response.setExpenseId(updatedExpense.getExpenseId());
	    response.setTitle(updatedExpense.getTitle());
	    response.setAmount(updatedExpense.getAmount());
	    response.setDescription(updatedExpense.getDescription());
	    response.setCategory(updatedExpense.getCategory());
	    response.setPaymentMethod(updatedExpense.getPaymentMethod());
	    response.setDate(updatedExpense.getDate());

	    return response;
	}


@Override
public void deleteExpenseById(
        Integer expenseId,
        Integer userId) {

    Expenses expense = expenseRepository
            .findByExpenseIdAndUser_Id(expenseId, userId)
            .orElseThrow(() ->
                    new RuntimeException("Expense not found"));

    expenseRepository.delete(expense);
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

@Override
public Page<Expenses> getExpenses(
        int page,
        int size,
        Integer userId) {

    Pageable pageable = PageRequest.of(page, size);

    return expenseRepository.findByUser_Id(userId, pageable);
}
}
