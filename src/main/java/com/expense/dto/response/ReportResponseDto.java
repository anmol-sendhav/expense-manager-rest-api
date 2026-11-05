package com.expense.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {

    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal savings;
    private BigDecimal savingsRate;

    private List<CategoryReport> categoryBreakdown;
    private List<MonthlyReport> monthlyTrend;
    private List<TransactionReport> recentTransactions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CategoryReport {
        private String category;
        private BigDecimal amount;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MonthlyReport {
        private String month;
        private BigDecimal income;
        private BigDecimal expenses;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TransactionReport {
        private String title;
        private String category;
        private BigDecimal amount;
        private LocalDate date;
        private String type;
    }
}