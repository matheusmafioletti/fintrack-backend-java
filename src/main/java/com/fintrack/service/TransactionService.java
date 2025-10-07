package com.fintrack.service;

import com.fintrack.dto.request.TransactionRequest;
import com.fintrack.dto.response.CategoryResponse;
import com.fintrack.dto.response.TransactionResponse;
import com.fintrack.dto.response.TransactionSummaryResponse;
import com.fintrack.exception.BadRequestException;
import com.fintrack.exception.ResourceNotFoundException;
import com.fintrack.model.Category;
import com.fintrack.model.Transaction;
import com.fintrack.model.User;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.repository.CategoryRepository;
import com.fintrack.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<TransactionResponse> getAllTransactions(TransactionType type,
                                                         Long categoryId,
                                                         LocalDate startDate,
                                                         LocalDate endDate,
                                                         String search,
                                                         Pageable pageable) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting transactions for user: {} with filters", currentUser.getEmail());

        Page<Transaction> transactions = transactionRepository.findByFilters(
                currentUser.getId(),
                type,
                categoryId,
                startDate,
                endDate,
                search,
                pageable
        );

        return transactions.map(this::mapToTransactionResponse);
    }

    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting transaction {} for user: {}", id, currentUser.getEmail());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Creating transaction for user: {}", currentUser.getEmail());

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Category not found or does not belong to user"));

        // Validate that transaction type matches category type
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("Transaction type must match category type");
        }

        Transaction transaction = Transaction.builder()
                .user(currentUser)
                .category(category)
                .description(request.getDescription())
                .amount(request.getAmount())
                .type(request.getType())
                .date(request.getDate())
                .notes(request.getNotes())
                .recurring(request.getRecurring() != null ? request.getRecurring() : false)
                .build();

        transaction = transactionRepository.save(transaction);
        log.info("Transaction created with id: {}", transaction.getId());

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionRequest request) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Updating transaction {} for user: {}", id, currentUser.getEmail());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new BadRequestException("Category not found or does not belong to user"));

        // Validate that transaction type matches category type
        if (!category.getType().equals(request.getType())) {
            throw new BadRequestException("Transaction type must match category type");
        }

        transaction.setCategory(category);
        transaction.setDescription(request.getDescription());
        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setDate(request.getDate());
        transaction.setNotes(request.getNotes());
        transaction.setRecurring(request.getRecurring() != null ? request.getRecurring() : false);

        transaction = transactionRepository.save(transaction);
        log.info("Transaction updated: {}", transaction.getId());

        return mapToTransactionResponse(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Deleting transaction {} for user: {}", id, currentUser.getEmail());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
        log.info("Transaction deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public TransactionSummaryResponse getTransactionSummary(LocalDate startDate, LocalDate endDate) {
        User currentUser = userService.getCurrentUserEntity();
        log.info("Getting transaction summary for user: {}", currentUser.getEmail());

        // If dates not provided, use current month
        if (startDate == null || endDate == null) {
            YearMonth currentMonth = YearMonth.now();
            startDate = currentMonth.atDay(1);
            endDate = currentMonth.atEndOfMonth();
        }

        BigDecimal totalIncome = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.INCOME, startDate, endDate);

        BigDecimal totalExpense = transactionRepository.sumByUserAndTypeAndDateBetween(
                currentUser.getId(), TransactionType.EXPENSE, startDate, endDate);

        BigDecimal balance = totalIncome.subtract(totalExpense);

        long transactionCount = transactionRepository.countByUserAndDateBetween(
                currentUser.getId(), startDate, endDate);

        TransactionSummaryResponse.PeriodInfo periodInfo = TransactionSummaryResponse.PeriodInfo.builder()
                .startDate(startDate)
                .endDate(endDate)
                .build();

        return TransactionSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .balance(balance)
                .transactionCount(transactionCount)
                .period(periodInfo)
                .build();
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(transaction.getCategory().getId())
                .name(transaction.getCategory().getName())
                .type(transaction.getCategory().getType())
                .color(transaction.getCategory().getColor())
                .icon(transaction.getCategory().getIcon())
                .build();

        return TransactionResponse.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .date(transaction.getDate())
                .notes(transaction.getNotes())
                .recurring(transaction.getRecurring())
                .category(categoryResponse)
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
