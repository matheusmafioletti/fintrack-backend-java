package com.fintrack.service;

import com.fintrack.dto.request.BudgetRequest;
import com.fintrack.dto.response.BudgetProgressResponse;
import com.fintrack.dto.response.BudgetResponse;
import com.fintrack.dto.response.CategoryResponse;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.model.Budget;
import com.fintrack.model.Category;
import com.fintrack.model.User;
import com.fintrack.model.enums.BudgetPeriod;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.repository.BudgetRepository;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<BudgetResponse> getAllBudgets(BudgetPeriod period) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting all budgets for user: {}", currentUser.getEmail());

        List<Budget> budgets;
        if (period != null) {
            budgets = budgetRepository.findByUserIdAndPeriod(currentUser.getId(), period);
        } else {
            budgets = budgetRepository.findByUserId(currentUser.getId());
        }

        return budgets.stream()
                .map(this::mapToBudgetResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BudgetResponse getBudgetById(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting budget {} for user: {}", id, currentUser.getEmail());

        Budget budget = budgetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        return mapToBudgetResponse(budget);
    }

    @Transactional
    public BudgetResponse createBudget(BudgetRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Creating budget for user: {}", currentUser.getEmail());

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Category not found or does not belong to user"));

        // Validate that category is EXPENSE type
        if (!category.getType().equals(TransactionType.EXPENSE)) {
            throw new BadRequestException("Budgets can only be created for expense categories");
        }

        // Validate no duplicate budget
        if (budgetRepository.existsByUserIdAndCategoryIdAndPeriod(
                currentUser.getId(), request.getCategoryId(), request.getPeriod())) {
            throw new BadRequestException("A budget already exists for this category and period");
        }

        // Validate dates
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        Budget budget = Budget.builder()
                .user(currentUser)
                .category(category)
                .amount(request.getAmount())
                .period(request.getPeriod())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        budget = budgetRepository.save(budget);
        log.info("Budget created with id: {}", budget.getId());

        return mapToBudgetResponse(budget);
    }

    @Transactional
    public BudgetResponse updateBudget(Long id, BudgetRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Updating budget {} for user: {}", id, currentUser.getEmail());

        Budget budget = budgetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Category not found or does not belong to user"));

        // Validate that category is EXPENSE type
        if (!category.getType().equals(TransactionType.EXPENSE)) {
            throw new BadRequestException("Budgets can only be created for expense categories");
        }

        // Validate no duplicate budget (excluding current)
        if (!budget.getCategory().getId().equals(request.getCategoryId()) ||
                !budget.getPeriod().equals(request.getPeriod())) {
            if (budgetRepository.existsByUserIdAndCategoryIdAndPeriodAndIdNot(
                    currentUser.getId(), request.getCategoryId(), request.getPeriod(), id)) {
                throw new BadRequestException("A budget already exists for this category and period");
            }
        }

        // Validate dates
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date must be after start date");
        }

        budget.setCategory(category);
        budget.setAmount(request.getAmount());
        budget.setPeriod(request.getPeriod());
        budget.setStartDate(request.getStartDate());
        budget.setEndDate(request.getEndDate());

        budget = budgetRepository.save(budget);
        log.info("Budget updated: {}", budget.getId());

        return mapToBudgetResponse(budget);
    }

    @Transactional
    public void deleteBudget(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Deleting budget {} for user: {}", id, currentUser.getEmail());

        Budget budget = budgetRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found"));

        budgetRepository.delete(budget);
        log.info("Budget deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public List<BudgetProgressResponse> getBudgetProgress() {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting budget progress for user: {}", currentUser.getEmail());

        List<Budget> budgets = budgetRepository.findByUserId(currentUser.getId());

        return budgets.stream()
                .map(this::calculateBudgetProgress)
                .collect(Collectors.toList());
    }

    private BudgetProgressResponse calculateBudgetProgress(Budget budget) {
        LocalDate now = LocalDate.now();
        
        // Determine period dates
        LocalDate periodStart;
        LocalDate periodEnd;

        switch (budget.getPeriod()) {
            case WEEKLY:
                periodStart = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                periodEnd = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
                break;
            case MONTHLY:
                periodStart = now.with(TemporalAdjusters.firstDayOfMonth());
                periodEnd = now.with(TemporalAdjusters.lastDayOfMonth());
                break;
            case YEARLY:
                periodStart = now.with(TemporalAdjusters.firstDayOfYear());
                periodEnd = now.with(TemporalAdjusters.lastDayOfYear());
                break;
            default:
                throw new IllegalStateException("Unknown budget period: " + budget.getPeriod());
        }

        // Get spent amount for the period and category
        BigDecimal categorySpent = transactionRepository.sumByUserAndCategoryAndTypeAndDateBetween(
                budget.getUser().getId(),
                budget.getCategory().getId(),
                TransactionType.EXPENSE,
                periodStart,
                periodEnd
        );

        BigDecimal remainingAmount = budget.getAmount().subtract(categorySpent);
        
        Double percentageUsed = budget.getAmount().compareTo(BigDecimal.ZERO) > 0
                ? categorySpent.divide(budget.getAmount(), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .doubleValue()
                : 0.0;

        BudgetProgressResponse.BudgetStatus status;
        if (percentageUsed >= 100) {
            status = BudgetProgressResponse.BudgetStatus.EXCEEDED;
        } else if (percentageUsed >= 80) {
            status = BudgetProgressResponse.BudgetStatus.WARNING;
        } else {
            status = BudgetProgressResponse.BudgetStatus.OK;
        }

        return BudgetProgressResponse.builder()
                .budgetId(budget.getId())
                .categoryName(budget.getCategory().getName())
                .categoryColor(budget.getCategory().getColor())
                .budgetAmount(budget.getAmount())
                .spentAmount(categorySpent)
                .remainingAmount(remainingAmount)
                .percentageUsed(Math.round(percentageUsed * 10.0) / 10.0)
                .status(status)
                .period(budget.getPeriod())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .build();
    }

    private BudgetResponse mapToBudgetResponse(Budget budget) {
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(budget.getCategory().getId())
                .name(budget.getCategory().getName())
                .type(budget.getCategory().getType())
                .color(budget.getCategory().getColor())
                .icon(budget.getCategory().getIcon())
                .build();

        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .period(budget.getPeriod())
                .startDate(budget.getStartDate())
                .endDate(budget.getEndDate())
                .category(categoryResponse)
                .createdAt(budget.getCreatedAt())
                .updatedAt(budget.getUpdatedAt())
                .build();
    }
}
