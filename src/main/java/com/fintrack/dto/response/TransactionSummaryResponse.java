package com.fintrack.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryResponse {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private Long transactionCount;
    private PeriodInfo period;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PeriodInfo {
        private LocalDate startDate;
        private LocalDate endDate;
    }
}
