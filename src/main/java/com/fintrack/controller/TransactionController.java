package com.fintrack.controller;

import com.fintrack.dto.request.TransactionRequest;
import com.fintrack.dto.response.TransactionResponse;
import com.fintrack.dto.response.TransactionSummaryResponse;
import com.fintrack.model.enums.TransactionType;
import com.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(required = false) TransactionType type,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "date,desc") String[] sort) {
        
        log.info("GET /api/transactions - Get all transactions with filters");
        
        // Parse sort parameters
        Sort.Direction direction = sort.length > 1 && sort[1].equalsIgnoreCase("asc") 
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(direction, sort[0]);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<TransactionResponse> transactions = transactionService.getAllTransactions(
                type, categoryId, startDate, endDate, search, pageable);
        
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {
        log.info("GET /api/transactions/{} - Get transaction by id", id);
        TransactionResponse transaction = transactionService.getTransactionById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody TransactionRequest request) {
        log.info("POST /api/transactions - Create new transaction");
        TransactionResponse transaction = transactionService.createTransaction(request);
        return new ResponseEntity<>(transaction, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request) {
        log.info("PUT /api/transactions/{} - Update transaction", id);
        TransactionResponse transaction = transactionService.updateTransaction(id, request);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        log.info("DELETE /api/transactions/{} - Delete transaction", id);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryResponse> getTransactionSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        log.info("GET /api/transactions/summary - Get transaction summary");
        TransactionSummaryResponse summary = transactionService.getTransactionSummary(startDate, endDate);
        return ResponseEntity.ok(summary);
    }
}
