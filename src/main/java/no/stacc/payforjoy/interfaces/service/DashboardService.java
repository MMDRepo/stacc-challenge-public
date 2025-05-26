package no.stacc.payforjoy.interfaces.service;

import no.stacc.payforjoy.model.dto.DashboardDto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;
import java.time.LocalDateTime;
import java.util.List;

public interface DashboardService {

    /**
     * Get complete dashboard data for a user
     */
    DashboardDto getDashboardData(Long userId);

    /**
     * Get dashboard data for a specific time period
     */
    DashboardDto getDashboardDataForPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get financial overview for dashboard
     */
    DashboardRepositoryImpl.FinancialOverview getFinancialOverview(Long userId);

    /**
     * Get savings analytics for dashboard
     */
    DashboardRepositoryImpl.SavingsAnalytics getSavingsAnalytics(Long userId);

    /**
     * Get spending analytics for dashboard
     */
    DashboardRepositoryImpl.SpendingAnalytics getSpendingAnalytics(Long userId);

    /**
     * Get monthly financial trends
     */
    List<DashboardRepositoryImpl.MonthlyTrend> getMonthlyTrends(Long userId, int monthsBack);

    /**
     * Refresh dashboard cache (if caching is implemented)
     */
    void refreshDashboardData(Long userId);

    /**
     * Get dashboard alerts for user
     */
    List<DashboardRepositoryImpl.DashboardAlert> getDashboardAlerts(Long userId);
}
