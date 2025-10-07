package com.fintrack.util;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private DateUtils() {
        // Private constructor to prevent instantiation
    }

    public static LocalDate getFirstDayOfMonth(YearMonth yearMonth) {
        return yearMonth.atDay(1);
    }

    public static LocalDate getLastDayOfMonth(YearMonth yearMonth) {
        return yearMonth.atEndOfMonth();
    }

    public static LocalDate getFirstDayOfCurrentMonth() {
        return getFirstDayOfMonth(YearMonth.now());
    }

    public static LocalDate getLastDayOfCurrentMonth() {
        return getLastDayOfMonth(YearMonth.now());
    }

    public static String formatYearMonth(YearMonth yearMonth) {
        return yearMonth.format(MONTH_FORMATTER);
    }

    public static YearMonth parseYearMonth(String yearMonth) {
        return YearMonth.parse(yearMonth, MONTH_FORMATTER);
    }

    public static boolean isDateInMonth(LocalDate date, YearMonth month) {
        return YearMonth.from(date).equals(month);
    }
}
