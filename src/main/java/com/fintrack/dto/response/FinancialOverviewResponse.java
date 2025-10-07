package com.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialOverviewResponse {

    private CurrentMonthSummary currentMonth;
    private List<CategorySummaryResponse> topCategories;
    private List<TransactionResponse> recentTransactions;
    private TotalAllTime totalAllTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CurrentMonthSummary {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal balance;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TotalAllTime {
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal balance;
    }
}
