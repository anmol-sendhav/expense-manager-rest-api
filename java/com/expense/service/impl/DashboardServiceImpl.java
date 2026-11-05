package com.expense.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expense.dto.response.DashboardResponseDto;
import com.expense.entity.User;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.IncomeRepository;
import com.expense.repository.UserRepository;
import com.expense.service.DashboardService;


@Service
	@Transactional(readOnly = true)
	public class DashboardServiceImpl implements DashboardService {

	    private final ExpenseRepository expenseRepository;
	    private final IncomeRepository incomeRepository;
	    private final UserRepository userRepository;

	    public DashboardServiceImpl(
	            ExpenseRepository expenseRepository,
	            IncomeRepository incomeRepository,
	            UserRepository userRepository) {
	        this.expenseRepository = expenseRepository;
	        this.incomeRepository = incomeRepository;
	        this.userRepository = userRepository;
	    }

	    @Override
	    public DashboardResponseDto getDashboardSummary(String email) {

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                    new IllegalArgumentException("User not found"));

	        System.out.println("Dashboard user ID: " + user.getId());
	        System.out.println("Dashboard email: " + user.getEmail());
	        Integer userId = user.getId();

	        BigDecimal totalIncome = incomeRepository
	                .getTotalIncomeByUserId(userId);

	        BigDecimal totalExpenses = expenseRepository
	                .getTotalExpensesByUserId(userId);


	        if (totalIncome == null) {
	            totalIncome = BigDecimal.ZERO;
	        }

	        if (totalExpenses == null) {
	            totalExpenses = BigDecimal.ZERO;
	        }


	        BigDecimal balance = totalIncome.subtract(totalExpenses);
	        BigDecimal savingsRate = BigDecimal.ZERO;

	        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
	            savingsRate = balance
	                    .multiply(BigDecimal.valueOf(100))
	                    .divide(totalIncome, 2, RoundingMode.HALF_UP);
	        }

	        return new DashboardResponseDto(
	        		user.getName(),
	                totalIncome,
	                totalExpenses,
	                balance,
	                savingsRate,
	                incomeRepository.countByUser_Id(userId),
	                expenseRepository.countByUser_Id(userId)
	        );
	    }
	}
