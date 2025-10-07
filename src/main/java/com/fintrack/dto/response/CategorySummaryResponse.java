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
public class CategorySummaryResponse {

    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private BigDecimal totalAmount;
    private Double percentage;
    private Long transactionCount;
}
