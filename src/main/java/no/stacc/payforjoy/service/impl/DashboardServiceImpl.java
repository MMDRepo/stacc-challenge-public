package no.stacc.payforjoy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.stacc.payforjoy.interfaces.repository.DashboardRepository;
import no.stacc.payforjoy.interfaces.service.*;
import no.stacc.payforjoy.model.dto.DashboardDto;
import no.stacc.payforjoy.model.dto.SavingsGoalDto;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import no.stacc.payforjoy.logging.PayForJoyLogger;

    @Service
    @Transactional(readOnly = true)
    @RequiredArgsConstructor
    @Slf4j
    public class DashboardServiceImpl implements DashboardService {

        private final AccountService accountService;
        private final TransactionService transactionService;
        private final SavingsGoalService savingsGoalService;
        private final RewardService rewardService;
        private final DashboardRepository dashboardRepository;
        private final PayForJoyLogger logger;

        @Override
        @Cacheable(value = "dashboardData", key = "#userId")
        public DashboardDto getDashboardData(Long userId) {
            log.info("Generating dashboard data for user: {}", userId);

            try {
                // Get financial overview
                DashboardRepositoryImpl.FinancialOverview financial = dashboardRepository.getFinancialOverview(userId);

                // Get recent transactions
                List<TransactionDto> recentTransactions = transactionService.findRecentByUserId(userId, 5);

                // Get active goals
                List<SavingsGoalDto> activeGoals = savingsGoalService.findActiveGoalsByUserId(userId);

                // Get goal counts
                long activeGoalsCount = savingsGoalService.countActiveGoals(userId);
                long completedGoalsCount = savingsGoalService.countCompletedGoals(userId);

                // Get total reward points
                Integer totalRewardPoints = rewardService.getTotalPoints(userId);

                // Calculate savings progress (average of all active goals)
                BigDecimal savingsProgress = calculateAverageSavingsProgress(activeGoals);

                DashboardDto dashboardDto = DashboardDto.builder()
                        .totalBalance(financial.getTotalBalance())
                        .monthlyIncome(financial.getMonthlyIncome())
                        .monthlyExpenses(financial.getMonthlyExpenses())
                        .totalSavings(financial.getTotalSavings())
                        .activeGoalsCount((int) activeGoalsCount)
                        .completedGoalsCount((int) completedGoalsCount)
                        .savingsProgress(savingsProgress)
                        .recentTransactions(recentTransactions)
                        .activeGoals(activeGoals)
                        .totalRewardPoints(totalRewardPoints)
                        .build();

                log.debug("Dashboard data generated successfully for user: {}", userId);
                return dashboardDto;

            } catch (Exception e) {
                log.error("Error generating dashboard data for user: {}", userId, e);
                throw new RuntimeException("Failed to generate dashboard data", e);
            }
        }

        @Override
        public DashboardDto getDashboardDataForPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
            log.info("Generating dashboard data for user: {} for period {} to {}", userId, startDate, endDate);

            // Get financial overview for period
            DashboardRepositoryImpl.FinancialOverview financial =
                    dashboardRepository.getFinancialOverviewForPeriod(userId, startDate, endDate);

            // Get transactions for period
            List<TransactionDto> periodTransactions =
                    dashboardRepository.getTransactionsForPeriod(userId, startDate, endDate, 10);

            // Get active goals (goals don't change by period)
            List<SavingsGoalDto> activeGoals = savingsGoalService.findActiveGoalsByUserId(userId);

            // Calculate savings progress
            BigDecimal savingsProgress = calculateAverageSavingsProgress(activeGoals);

            return DashboardDto.builder()
                    .totalBalance(financial.getTotalBalance())
                    .monthlyIncome(financial.getMonthlyIncome())
                    .monthlyExpenses(financial.getMonthlyExpenses())
                    .totalSavings(financial.getTotalSavings())
                    .activeGoalsCount(activeGoals.size())
                    .completedGoalsCount((int) savingsGoalService.countCompletedGoals(userId))
                    .savingsProgress(savingsProgress)
                    .recentTransactions(periodTransactions)
                    .activeGoals(activeGoals)
                    .totalRewardPoints(rewardService.getTotalPoints(userId))
                    .build();
        }

        @Override
        public DashboardRepositoryImpl.FinancialOverview getFinancialOverview(Long userId) {
            log.debug("Getting financial overview for user: {}", userId);
            return dashboardRepository.getFinancialOverview(userId);
        }

        @Override
        public DashboardRepositoryImpl.SavingsAnalytics getSavingsAnalytics(Long userId) {
            log.debug("Getting savings analytics for user: {}", userId);
            return dashboardRepository.getSavingsAnalytics(userId);
        }

        @Override
        public DashboardRepositoryImpl.SpendingAnalytics getSpendingAnalytics(Long userId) {
            log.debug("Getting spending analytics for user: {}", userId);
            return dashboardRepository.getSpendingAnalytics(userId);
        }

        @Override
        public List<DashboardRepositoryImpl.MonthlyTrend> getMonthlyTrends(Long userId, int monthsBack) {
            log.debug("Getting monthly trends for user: {} for {} months", userId, monthsBack);
            return dashboardRepository.getMonthlyTrends(userId, monthsBack);
        }

        @Override
        @Transactional
        public void refreshDashboardData(Long userId) {
            log.info("Refreshing dashboard data for user: {}", userId);
            // If using caching, clear the cache here
            // cacheManager.evict("dashboardData", userId);
        }

        @Override
        public List<DashboardRepositoryImpl.DashboardAlert> getDashboardAlerts(Long userId) {
            log.debug("Getting dashboard alerts for user: {}", userId);
            return dashboardRepository.getDashboardAlerts(userId);
        }

        private BigDecimal calculateAverageSavingsProgress(List<SavingsGoalDto> activeGoals) {
            if (activeGoals.isEmpty()) {
                return BigDecimal.ZERO;
            }

            BigDecimal totalProgress = activeGoals.stream()
                    .map(SavingsGoalDto::getProgressPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            return totalProgress.divide(BigDecimal.valueOf(activeGoals.size()), 2, BigDecimal.ROUND_HALF_UP);

        }
    }

