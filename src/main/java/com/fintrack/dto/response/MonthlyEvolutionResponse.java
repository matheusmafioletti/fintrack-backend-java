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
public class MonthlyEvolutionResponse {

    private String month;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
}
