package com.expense.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.expense.dto.response.ReportResponseDto;
import com.expense.entity.Expenses;
import com.expense.entity.Income;
import com.expense.entity.User;
import com.expense.repository.ExpenseRepository;
import com.expense.repository.IncomeRepository;
import com.expense.repository.UserRepository;
import com.expense.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportServiceImpl implements ReportService {

    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    @Override
    public ReportResponseDto getReport(
            String email,
            LocalDate startDate,
            LocalDate endDate) {

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Invalid report date range");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Integer userId = user.getId();

        List<Expenses> expenses = expenseRepository.findByUser_Id(userId)
                .stream()
                .filter(e -> e.getDate() != null
                        && !e.getDate().isBefore(startDate)
                        && !e.getDate().isAfter(endDate))
                .toList();

        List<Income> incomes = incomeRepository.findByUser_Id(userId)
                .stream()
                .filter(i -> i.getAmountDate() != null
                        && !i.getAmountDate().isBefore(startDate)
                        && !i.getAmountDate().isAfter(endDate))
                .toList();

        BigDecimal totalExpenses = expenses.stream()
                .map(Expenses::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalIncome = incomes.stream()
                .map(Income::getAmount)
                .filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal savings = totalIncome.subtract(totalExpenses);

        BigDecimal savingsRate = BigDecimal.ZERO;

        if (totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            savingsRate = savings.multiply(BigDecimal.valueOf(100))
                    .divide(totalIncome, 2, RoundingMode.HALF_UP);
        }

        // Category-wise expense breakdown
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();

        for (Expenses expense : expenses) {
            String category = expense.getCategory() == null
                    ? "Other"
                    : expense.getCategory().name();

            categoryTotals.merge(
                    category,
                    expense.getAmount(),
                    BigDecimal::add
            );
        }

        List<ReportResponseDto.CategoryReport> categoryBreakdown =
                categoryTotals.entrySet().stream()
                        .map(e -> new ReportResponseDto.CategoryReport(
                                e.getKey(), e.getValue()))
                        .sorted(Comparator.comparing(
                                ReportResponseDto.CategoryReport::getAmount)
                                .reversed())
                        .toList();

        // Monthly income and expense comparison
        Map<YearMonth, BigDecimal> monthlyIncome = new TreeMap<>();
        Map<YearMonth, BigDecimal> monthlyExpenses = new TreeMap<>();

        for (Income income : incomes) {
            YearMonth month = YearMonth.from(income.getAmountDate());
            monthlyIncome.merge(month, income.getAmount(), BigDecimal::add);
        }

        for (Expenses expense : expenses) {
            YearMonth month = YearMonth.from(expense.getDate());
            monthlyExpenses.merge(month, expense.getAmount(), BigDecimal::add);
        }

        List<ReportResponseDto.MonthlyReport> monthlyTrend =
                new ArrayList<>();

        YearMonth current = YearMonth.from(startDate);
        YearMonth last = YearMonth.from(endDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy");

        while (!current.isAfter(last)) {
            monthlyTrend.add(
                    new ReportResponseDto.MonthlyReport(
                            current.format(formatter),
                            monthlyIncome.getOrDefault(current, BigDecimal.ZERO),
                            monthlyExpenses.getOrDefault(current, BigDecimal.ZERO)
                    )
            );

            current = current.plusMonths(1);
        }

        // Recent transactions
        List<ReportResponseDto.TransactionReport> recentTransactions =
                new ArrayList<>();

        expenses.forEach(e -> recentTransactions.add(
                new ReportResponseDto.TransactionReport(
                        e.getTitle(),
                        e.getCategory() == null ? "Other" : e.getCategory().name(),
                        e.getAmount(),
                        e.getDate(),
                        "EXPENSE"
                )
        ));

        incomes.forEach(i -> recentTransactions.add(
                new ReportResponseDto.TransactionReport(
                        i.getTitle(),
                        i.getSource(),
                        i.getAmount(),
                        i.getAmountDate(),
                        "INCOME"
                )
        ));

        recentTransactions.sort(
                Comparator.comparing(
                        ReportResponseDto.TransactionReport::getDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                )
        );

        List<ReportResponseDto.TransactionReport> limitedTransactions =
                recentTransactions.stream()
                        .limit(10)
                        .toList();

        return new ReportResponseDto(
                totalIncome,
                totalExpenses,
                savings,
                savingsRate,
                categoryBreakdown,
                monthlyTrend,
                limitedTransactions
        );
    }
}