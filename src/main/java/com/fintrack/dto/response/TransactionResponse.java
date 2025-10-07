package com.fintrack.dto.response;

import com.fintrack.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {

    private Long id;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate date;
    private String notes;
    private Boolean recurring;
    private CategoryResponse category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
