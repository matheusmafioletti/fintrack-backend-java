package com.fintrack.util;

import com.fintrack.exception.BadRequestException;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ValidationUtils {

    private ValidationUtils() {
        // Private constructor to prevent instantiation
    }

    public static void validatePositiveAmount(BigDecimal amount, String fieldName) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(fieldName + " must be positive");
        }
    }

    public static void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new BadRequestException("End date must be after start date");
        }
    }

    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
    }

    public static void validateStringLength(String value, int min, int max, String fieldName) {
        if (value == null) {
            throw new BadRequestException(fieldName + " is required");
        }
        if (value.length() < min || value.length() > max) {
            throw new BadRequestException(fieldName + " must be between " + min + " and " + max + " characters");
        }
    }

    public static void validateFutureDate(LocalDate date, String fieldName) {
        if (date != null && date.isBefore(LocalDate.now())) {
            throw new BadRequestException(fieldName + " must be in the future");
        }
    }

    public static void validatePastOrPresentDate(LocalDate date, String fieldName) {
        if (date != null && date.isAfter(LocalDate.now())) {
            throw new BadRequestException(fieldName + " cannot be in the future");
        }
    }
}
