package com.fintrack.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(ValidDateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDate();
        this.endDateField = constraintAnnotation.endDate();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        LocalDate startDate = (LocalDate) beanWrapper.getPropertyValue(startDateField);
        LocalDate endDate = (LocalDate) beanWrapper.getPropertyValue(endDateField);

        if (startDate == null || endDate == null) {
            return true;
        }

        return endDate.isAfter(startDate);
    }
}
