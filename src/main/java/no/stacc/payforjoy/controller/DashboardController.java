package no.stacc.payforjoy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.stacc.payforjoy.constants.ApiEndPoints;
import no.stacc.payforjoy.interfaces.service.DashboardService;
import no.stacc.payforjoy.model.dto.DashboardDto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping( "/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<DashboardDto> getDashboardData(@PathVariable Long userId) {
        log.info("Dashboard data requested for user: {}", userId);
        DashboardDto dashboardData = dashboardService.getDashboardData(userId);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/user/{userId}/period")
    public ResponseEntity<DashboardDto> getDashboardDataForPeriod(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Dashboard data requested for user: {} for period {} to {}", userId, startDate, endDate);
        DashboardDto dashboardData = dashboardService.getDashboardDataForPeriod(userId, startDate, endDate);
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/user/{userId}/financial-overview")
    public ResponseEntity<DashboardRepositoryImpl.FinancialOverview> getFinancialOverview(@PathVariable Long userId) {
        DashboardRepositoryImpl.FinancialOverview overview = dashboardService.getFinancialOverview(userId);
        return ResponseEntity.ok(overview);
    }

    @GetMapping("/user/{userId}/savings-analytics")
    public ResponseEntity<DashboardRepositoryImpl.SavingsAnalytics> getSavingsAnalytics(@PathVariable Long userId) {
        DashboardRepositoryImpl.SavingsAnalytics analytics = dashboardService.getSavingsAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/user/{userId}/spending-analytics")
    public ResponseEntity<DashboardRepositoryImpl.SpendingAnalytics> getSpendingAnalytics(@PathVariable Long userId) {
        DashboardRepositoryImpl.SpendingAnalytics analytics = dashboardService.getSpendingAnalytics(userId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/user/{userId}/trends")
    public ResponseEntity<List<DashboardRepositoryImpl.MonthlyTrend>> getMonthlyTrends(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "12") int monthsBack) {
        List<DashboardRepositoryImpl.MonthlyTrend> trends = dashboardService.getMonthlyTrends(userId, monthsBack);
        return ResponseEntity.ok(trends);
    }

    @GetMapping("/user/{userId}/alerts")
    public ResponseEntity<List<DashboardRepositoryImpl.DashboardAlert>> getDashboardAlerts(@PathVariable Long userId) {
        List<DashboardRepositoryImpl.DashboardAlert> alerts = dashboardService.getDashboardAlerts(userId);
        return ResponseEntity.ok(alerts);
    }

    @PostMapping("/user/{userId}/refresh")
    public ResponseEntity<Void> refreshDashboardData(@PathVariable Long userId) {
        log.info("Refresh dashboard data requested for user: {}", userId);
        dashboardService.refreshDashboardData(userId);
        return ResponseEntity.ok().build();
    }
}