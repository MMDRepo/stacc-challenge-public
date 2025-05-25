package no.stacc.payforjoy.interfaces.repository;

import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;

import java.time.LocalDateTime;
import java.util.List;

public interface DashboardRepository {

    /**
     * Get comprehensive financial overview for a user
     */
    DashboardRepositoryImpl.FinancialOverview getFinancialOverview(Long userId);

    /**
     * Get financial overview for a specific period
     */
    DashboardRepositoryImpl.FinancialOverview getFinancialOverviewForPeriod(
            Long userId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get savings analytics
     */
    DashboardRepositoryImpl.SavingsAnalytics getSavingsAnalytics(Long userId);

    /**
     * Get spending analytics
     */
    DashboardRepositoryImpl.SpendingAnalytics getSpendingAnalytics(Long userId);

    /**
     * Get monthly financial trends
     */
    List<DashboardRepositoryImpl.MonthlyTrend> getMonthlyTrends(Long userId, int monthsBack);

    /**
     * Get transactions for a specific period
     */
    List<TransactionDto> getTransactionsForPeriod(Long userId, LocalDateTime startDate,
                                                  LocalDateTime endDate, int limit);

    /**
     * Get dashboard alerts
     */
    List<DashboardRepositoryImpl.DashboardAlert> getDashboardAlerts(Long userId);

    /**
     * Get account balance history
     */
    List<DashboardRepositoryImpl.BalanceHistory> getBalanceHistory(Long userId, int daysBack);

    /**
     * Get category-wise spending trends
     */
    List<DashboardRepositoryImpl.CategoryTrend> getCategoryTrends(Long userId, int monthsBack);
}