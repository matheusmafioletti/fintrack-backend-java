package com.fintrack.dto.response;

import com.fintrack.model.enums.BudgetPeriod;
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
public class BudgetProgressResponse {

    private Long budgetId;
    private String categoryName;
    private String categoryColor;
    private BigDecimal budgetAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private Double percentageUsed;
    private BudgetStatus status;
    private BudgetPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;

    public enum BudgetStatus {
        OK,         // < 80%
        WARNING,    // 80-100%
        EXCEEDED    // > 100%
    }
}
