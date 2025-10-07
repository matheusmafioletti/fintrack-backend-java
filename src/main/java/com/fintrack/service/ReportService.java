package com.fintrack.service;

import com.fintrack.dto.response.*;
import com.fintrack.model.User;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final TransactionService transactionService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    @Transactional(readOnly = true)
    public MonthSummaryResponse getMonthSummary() {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting month summary for user: {}", currentUser.getEmail());

        YearMonth currentMonth = YearMonth.now();
        YearMonth previousMonth = currentMonth.minusMonths(1);

        LocalDate currentStart = currentMonth.atDay(1);
        LocalDate currentEnd = currentMonth.atEndOfMonth();
        LocalDate previousStart = previousMonth.atDay(1);
        LocalDate previousEnd = previousMonth.atEndOfMonth();

        BigDecimal currentIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.INCOME, currentStart, currentEnd);
        BigDecimal currentExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.EXPENSE, currentStart, currentEnd);
        BigDecimal currentBalance = currentIncome.subtract(currentExpense);

        BigDecimal previousIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.INCOME, previousStart, previousEnd);
        BigDecimal previousExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.EXPENSE, previousStart, previousEnd);
        BigDecimal previousBalance = previousIncome.subtract(previousExpense);

        MonthSummaryResponse.ComparisonData comparison = MonthSummaryResponse.ComparisonData.builder()
                .incomeChange(calculatePercentageChange(previousIncome, currentIncome))
                .expenseChange(calculatePercentageChange(previousExpense, currentExpense))
                .balanceChange(calculatePercentageChange(previousBalance, currentBalance))
                .build();

        return MonthSummaryResponse.builder()
                .month(currentMonth.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .totalIncome(currentIncome)
                .totalExpense(currentExpense)
                .balance(currentBalance)
                .comparisonWithPreviousMonth(comparison)
                .build();
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryResponse> getCategorySummary(String month) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting category summary for user: {} and month: {}", currentUser.getEmail(), month);

        YearMonth yearMonth = month != null ? YearMonth.parse(month) : YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        String sql = "SELECT c.id, c.name, c.color, SUM(t.amount) as total, COUNT(t.id) as count " +
                     "FROM transactions t " +
                     "INNER JOIN categories c ON t.category_id = c.id " +
                     "WHERE t.user_id = ? AND t.type = 'EXPENSE' AND t.date >= ? AND t.date <= ? " +
                     "GROUP BY c.id, c.name, c.color " +
                     "ORDER BY total DESC";

        List<CategorySummaryResponse> summaries = jdbcTemplate.query(sql,
                (rs, rowNum) -> CategorySummaryResponse.builder()
                        .categoryId(rs.getLong("id"))
                        .categoryName(rs.getString("name"))
                        .categoryColor(rs.getString("color"))
                        .totalAmount(rs.getBigDecimal("total"))
                        .transactionCount(rs.getLong("count"))
                        .build(),
                currentUser.getId(), startDate, endDate);

        // Calculate percentages
        BigDecimal totalExpense = summaries.stream()
                .map(CategorySummaryResponse::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalExpense.compareTo(BigDecimal.ZERO) > 0) {
            summaries.forEach(summary -> {
                double percentage = summary.getTotalAmount()
                        .divide(totalExpense, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue();
                summary.setPercentage(Math.round(percentage * 10.0) / 10.0);
            });
        }

        return summaries;
    }

    @Transactional(readOnly = true)
    public List<MonthlyEvolutionResponse> getMonthlyEvolution(Integer months) {
        User currentUser = userService.getCurrentUserEntity();
        int monthsToShow = months != null && months > 0 && months <= 24 ? months : 12;
        log.info("Getting monthly evolution for user: {} for last {} months", currentUser.getEmail(), monthsToShow);

        List<MonthlyEvolutionResponse> evolution = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();

        for (int i = monthsToShow - 1; i >= 0; i--) {
            YearMonth month = currentMonth.minusMonths(i);
            LocalDate startDate = month.atDay(1);
            LocalDate endDate = month.atEndOfMonth();

            BigDecimal income = transactionRepository.sumByUserAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.INCOME, startDate, endDate);
            BigDecimal expense = transactionRepository.sumByUserAndTypeAndDateBetween(
                    currentUser.getId(), TransactionType.EXPENSE, startDate, endDate);
            BigDecimal balance = income.subtract(expense);

            evolution.add(MonthlyEvolutionResponse.builder()
                    .month(month.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                    .income(income)
                    .expense(expense)
                    .balance(balance)
                    .build());
        }

        return evolution;
    }

    @Transactional(readOnly = true)
    public FinancialOverviewResponse getFinancialOverview() {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting financial overview for user: {}", currentUser.getEmail());

        // Current month summary
        YearMonth currentMonth = YearMonth.now();
        LocalDate currentStart = currentMonth.atDay(1);
        LocalDate currentEnd = currentMonth.atEndOfMonth();

        BigDecimal currentIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.INCOME, currentStart, currentEnd);
        BigDecimal currentExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.EXPENSE, currentStart, currentEnd);
        BigDecimal currentBalance = currentIncome.subtract(currentExpense);

        FinancialOverviewResponse.CurrentMonthSummary currentMonthSummary =
                FinancialOverviewResponse.CurrentMonthSummary.builder()
                        .totalIncome(currentIncome)
                        .totalExpense(currentExpense)
                        .balance(currentBalance)
                        .build();

        // Top 5 categories
        List<CategorySummaryResponse> topCategories = getCategorySummary(null).stream()
                .limit(5)
                .toList();

        // Recent 5 transactions
        List<TransactionResponse> recentTransactions = transactionService.getAllTransactions(
                null, null, null, null, null,
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "date", "createdAt"))
        ).getContent();

        // All time totals
        String allTimeSql = "SELECT " +
                            "COALESCE(SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END), 0) as income, " +
                            "COALESCE(SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END), 0) as expense " +
                            "FROM transactions WHERE user_id = ?";

        FinancialOverviewResponse.TotalAllTime totalAllTime = jdbcTemplate.queryForObject(allTimeSql,
                (rs, rowNum) -> {
                    BigDecimal income = rs.getBigDecimal("income");
                    BigDecimal expense = rs.getBigDecimal("expense");
                    return FinancialOverviewResponse.TotalAllTime.builder()
                            .income(income)
                            .expense(expense)
                            .balance(income.subtract(expense))
                            .build();
                },
                currentUser.getId());

        return FinancialOverviewResponse.builder()
                .currentMonth(currentMonthSummary)
                .topCategories(topCategories)
                .recentTransactions(recentTransactions)
                .totalAllTime(totalAllTime)
                .build();
    }

    private Double calculatePercentageChange(BigDecimal oldValue, BigDecimal newValue) {
        if (oldValue.compareTo(BigDecimal.ZERO) == 0) {
            return newValue.compareTo(BigDecimal.ZERO) == 0 ? 0.0 : 100.0;
        }
        BigDecimal change = newValue.subtract(oldValue)
                .divide(oldValue, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        return Math.round(change.doubleValue() * 10.0) / 10.0;
    }
}
