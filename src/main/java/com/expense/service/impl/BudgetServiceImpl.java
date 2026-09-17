package com.expense.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.expense.dto.request.BudgetRequestDto;
import com.expense.dto.request.BudgetUsageDto;
import com.expense.dto.response.BudgetResponseDto;
import com.expense.entity.Budget;
import com.expense.entity.User;
import com.expense.exception.ResourceNotFoundException;
import com.expense.mapper.BudgetMapper;
import com.expense.repository.BudgetRepository;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.UserRepository;
import com.expense.service.BudgetService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class BudgetServiceImpl implements BudgetService {

    private  final UserRepository userRepository;
    private  final BudgetRepository budgetRepository;
    private  final ExpenseRepository expenseRepository;
    private final BudgetMapper mapper;

	@Override
	public BudgetResponseDto saveBudget(BudgetRequestDto dto) {
		Budget budget = mapper.toEntity(dto);
		User user = userRepository.findByEmail(
	            SecurityContextHolder.getContext()
	                    .getAuthentication()
	                    .getName()
	    ).orElseThrow(() -> new ResourceNotFoundException("User not found"));

	    budget.setUser(user);
		Budget savedBudget=budgetRepository.save(budget);
		return mapper.toDto(savedBudget);
	}
	
	@Override
	public List<BudgetResponseDto> getAllBudgetsForUser() {
		List<Budget> budgets=budgetRepository.findAll();
		return mapper.toDto(budgets);
	}

	@Override
	public BudgetResponseDto getBudgetById(int id) {
		Budget budgets=budgetRepository.findById(id).orElseThrow();
		return mapper.toDto(budgets);
	}

	@Override
	public BudgetResponseDto updateBudget(BudgetRequestDto request, int id) {
		Budget budget = budgetRepository.findById(id)
		.orElseThrow(() -> new RuntimeException());
		mapper.updateBudgetFromDto(request, budget);
		Budget updated = budgetRepository.save(budget);
		return mapper.toDto(updated);
	}

	@Override
	public void deleteBudgetById(int id) {
		this.budgetRepository.deleteById(id);
	}

	@Override
	public BudgetUsageDto getBudgetUsage(int budgetID) {
		Budget budget = budgetRepository.findById(budgetID)
				.orElseThrow(()-> new ResourceNotFoundException("Budget Not Found with"+budgetID));
		
		LocalDate month=budget.getMonth();
		LocalDate start=month.withDayOfMonth(1);
		LocalDate end=start.plusMonths(1);
		
		BigDecimal usedAmount =expenseRepository.getTotalExpenseForBudget(
                        budget.getUser().getId(),
                        budget.getCategory(),
                        start,
                        end);

        BigDecimal budgetAmount =budget.getBudgetAmount();
        BigDecimal remainingAmount =budgetAmount.subtract(usedAmount);
        BigDecimal usagePercentage;

        if (budgetAmount.compareTo(BigDecimal.ZERO) == 0) {
            usagePercentage = BigDecimal.ZERO;
        }else{
            usagePercentage =usedAmount
                        .multiply(BigDecimal.valueOf(100))
                        .divide(budgetAmount,2,RoundingMode.HALF_UP);
        }
        String status;

        if (usagePercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            status = "EXCEEDED";
        } else if (usagePercentage.compareTo(
                BigDecimal.valueOf(80)) >= 0) {
            status = "WARNING";
        } else {
            status = "SAFE";
        }
        BudgetUsageDto dto = new BudgetUsageDto();

        dto.setBudgetId(budget.getId());
        dto.setBudgetAmount(budgetAmount);
        dto.setUsedAmount(usedAmount);
        dto.setRemainingAmount(remainingAmount);
        dto.setUsagePercentage(usagePercentage);
        dto.setStatus(status);

        return dto;
	}
}
