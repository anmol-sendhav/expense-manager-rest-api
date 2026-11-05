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

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final ExpenseRepository expenseRepository;
    private final BudgetMapper mapper;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));
    }

    private Budget getOwnedBudget(Integer id, Integer userId) {
        return budgetRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Budget not found"));
    }

    @Override
    public BudgetResponseDto saveBudget(BudgetRequestDto dto) {
        User user = getCurrentUser();

        Budget budget = mapper.toEntity(dto);
        budget.setUser(user);

        return mapper.toDto(budgetRepository.save(budget));
    }

    @Override
    public List<BudgetResponseDto> getAllBudgetsForUser() {
        User user = getCurrentUser();

        return mapper.toDto(
                budgetRepository.findAllByUser_Id(user.getId()));
    }

    @Override
    public BudgetResponseDto getBudgetById(int id) {
        User user = getCurrentUser();

        return mapper.toDto(getOwnedBudget(id, user.getId()));
    }

    @Override
    public BudgetResponseDto updateBudget(
            BudgetRequestDto request, int id) {

        User user = getCurrentUser();
        Budget budget = getOwnedBudget(id, user.getId());

        mapper.updateBudgetFromDto(request, budget);

        return mapper.toDto(budgetRepository.save(budget));
    }

    @Override
    public void deleteBudgetById(int id) {
        User user = getCurrentUser();
        Budget budget = getOwnedBudget(id, user.getId());

        budgetRepository.delete(budget);
    }

    @Override
    public BudgetUsageDto getBudgetUsage(int budgetId) {
        User user = getCurrentUser();
        Budget budget = getOwnedBudget(budgetId, user.getId());

        LocalDate start = budget.getMonth().withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        BigDecimal usedAmount =
                expenseRepository.getTotalExpenseForBudget(
                        user.getId(),
                        budget.getCategory(),
                        start,
                        end);

        if (usedAmount == null) {
            usedAmount = BigDecimal.ZERO;
        }

        BigDecimal budgetAmount = budget.getBudgetAmount();
        BigDecimal remainingAmount =
                budgetAmount.subtract(usedAmount);

        BigDecimal usagePercentage = BigDecimal.ZERO;

        if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
            usagePercentage = usedAmount
                    .multiply(BigDecimal.valueOf(100))
                    .divide(budgetAmount, 2, RoundingMode.HALF_UP);
        }

        String status;

        if (usagePercentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            status = "EXCEEDED";
        } else if (usagePercentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
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