package com.fintrack.controller;

import com.fintrack.dto.request.BudgetRequest;
import com.fintrack.dto.response.BudgetProgressResponse;
import com.fintrack.dto.response.BudgetResponse;
import com.fintrack.model.enums.BudgetPeriod;
import com.fintrack.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public ResponseEntity<List<BudgetResponse>> getAllBudgets(
            @RequestParam(required = false) BudgetPeriod period) {
        log.info("GET /api/budgets - Get all budgets (period: {})", period);
        List<BudgetResponse> budgets = budgetService.getAllBudgets(period);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BudgetResponse> getBudgetById(@PathVariable Long id) {
        log.info("GET /api/budgets/{} - Get budget by id", id);
        BudgetResponse budget = budgetService.getBudgetById(id);
        return ResponseEntity.ok(budget);
    }

    @PostMapping
    public ResponseEntity<BudgetResponse> createBudget(@Valid @RequestBody BudgetRequest request) {
        log.info("POST /api/budgets - Create new budget");
        BudgetResponse budget = budgetService.createBudget(request);
        return new ResponseEntity<>(budget, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BudgetResponse> updateBudget(
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        log.info("PUT /api/budgets/{} - Update budget", id);
        BudgetResponse budget = budgetService.updateBudget(id, request);
        return ResponseEntity.ok(budget);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        log.info("DELETE /api/budgets/{} - Delete budget", id);
        budgetService.deleteBudget(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/progress")
    public ResponseEntity<List<BudgetProgressResponse>> getBudgetProgress() {
        log.info("GET /api/budgets/progress - Get budget progress");
        List<BudgetProgressResponse> progress = budgetService.getBudgetProgress();
        return ResponseEntity.ok(progress);
    }
}
