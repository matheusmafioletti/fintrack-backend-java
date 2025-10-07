package com.fintrack.controller;

import com.fintrack.dto.response.*;
import com.fintrack.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/month-summary")
    public ResponseEntity<MonthSummaryResponse> getMonthSummary() {
        log.info("GET /api/reports/month-summary - Get month summary");
        MonthSummaryResponse summary = reportService.getMonthSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/category-summary")
    public ResponseEntity<List<CategorySummaryResponse>> getCategorySummary(
            @RequestParam(required = false) String month) {
        log.info("GET /api/reports/category-summary - Get category summary for month: {}", month);
        List<CategorySummaryResponse> summary = reportService.getCategorySummary(month);
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/monthly-evolution")
    public ResponseEntity<List<MonthlyEvolutionResponse>> getMonthlyEvolution(
            @RequestParam(required = false, defaultValue = "12") Integer months) {
        log.info("GET /api/reports/monthly-evolution - Get monthly evolution for {} months", months);
        List<MonthlyEvolutionResponse> evolution = reportService.getMonthlyEvolution(months);
        return ResponseEntity.ok(evolution);
    }

    @GetMapping("/overview")
    public ResponseEntity<FinancialOverviewResponse> getFinancialOverview() {
        log.info("GET /api/reports/overview - Get financial overview");
        FinancialOverviewResponse overview = reportService.getFinancialOverview();
        return ResponseEntity.ok(overview);
    }
}
