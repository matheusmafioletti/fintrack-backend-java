package com.fintrack.dto.request;

import com.fintrack.model.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 200, message = "Description must be between 3 and 200 characters")
    private String description;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    private String notes;

    private Boolean recurring;
}
