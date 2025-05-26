package no.stacc.payforjoy.model.dto;
import no.stacc.payforjoy.repository.impl.DashboardRepositoryImpl;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    // Basic financial data
    private BigDecimal totalBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpenses;
    private BigDecimal totalSavings;
    private BigDecimal netIncome;

    // Goals data
    private int activeGoalsCount;
    private int completedGoalsCount;
    private BigDecimal savingsProgress;
    private List<SavingsGoalDto> activeGoals;

    // Transactions data
    private List<TransactionDto> recentTransactions;

    // Rewards data
    private int totalRewardPoints;
    private int unclaimedRewards;

    // Analytics data
    private List<DashboardRepositoryImpl.MonthlyTrend> monthlyTrends;
    private List<DashboardRepositoryImpl.CategorySpending> topSpendingCategories;
    private List<DashboardRepositoryImpl.DashboardAlert> alerts;

    // Performance indicators
    private BigDecimal savingsRate;
    private BigDecimal spendingGrowth;
    private int achievedGoalsThisMonth;
}
