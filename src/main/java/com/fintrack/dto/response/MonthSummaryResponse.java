package com.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthSummaryResponse {

    private String month;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private ComparisonData comparisonWithPreviousMonth;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ComparisonData {
        private Double incomeChange;
        private Double expenseChange;
        private Double balanceChange;
    }
}
