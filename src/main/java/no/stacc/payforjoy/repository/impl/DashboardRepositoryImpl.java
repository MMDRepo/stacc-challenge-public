package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.enums.GoalStatus;
import no.stacc.payforjoy.enums.TransactionType;
import no.stacc.payforjoy.interfaces.repository.DashboardRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.extern.slf4j.Slf4j;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.model.entity.Transaction;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional(readOnly = true)
@Slf4j
public class DashboardRepositoryImpl implements DashboardRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public FinancialOverview getFinancialOverview(Long userId) {
        log.debug("Getting financial overview for user: {}", userId);

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        // Get total balance
        String balanceQuery = "SELECT SUM(a.balance) FROM Account a WHERE a.user.id = :userId";
        BigDecimal totalBalance = (BigDecimal) entityManager.createQuery(balanceQuery)
                .setParameter("userId", userId)
                .getSingleResult();

        // Get monthly income and expenses
        String monthlyQuery = "SELECT t.transactionType, SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate >= :startOfMonth " +
                "GROUP BY t.transactionType";

        List<Object[]> monthlyResults = entityManager.createQuery(monthlyQuery)
                .setParameter("userId", userId)
                .setParameter("startOfMonth", startOfMonth)
                .getResultList();

        BigDecimal monthlyIncome = BigDecimal.ZERO;
        BigDecimal monthlyExpenses = BigDecimal.ZERO;

        for (Object[] result : monthlyResults) {
            TransactionType type = (TransactionType) result[0];
            BigDecimal amount = (BigDecimal) result[1];

            if (type == TransactionType.INCOME) {
                monthlyIncome = amount;
            } else if (type == TransactionType.EXPENSE) {
                monthlyExpenses = amount;
            }
        }

        // Get total savings from savings goals
        String savingsQuery = "SELECT SUM(sg.currentAmount) FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId AND sg.status = :status";
        BigDecimal totalSavings = (BigDecimal) entityManager.createQuery(savingsQuery)
                .setParameter("userId", userId)
                .setParameter("status", GoalStatus.ACTIVE)
                .getSingleResult();

        return FinancialOverview.builder()
                .totalBalance(totalBalance != null ? totalBalance : BigDecimal.ZERO)
                .monthlyIncome(monthlyIncome)
                .monthlyExpenses(monthlyExpenses)
                .totalSavings(totalSavings != null ? totalSavings : BigDecimal.ZERO)
                .netIncome(monthlyIncome.subtract(monthlyExpenses))
                .build();
    }

    @Override
    public FinancialOverview getFinancialOverviewForPeriod(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Getting financial overview for user: {} for period {} to {}", userId, startDate, endDate);

        String periodQuery = "SELECT t.transactionType, SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                "GROUP BY t.transactionType";

        List<Object[]> results = entityManager.createQuery(periodQuery)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        BigDecimal periodIncome = BigDecimal.ZERO;
        BigDecimal periodExpenses = BigDecimal.ZERO;

        for (Object[] result : results) {
            TransactionType type = (TransactionType) result[0];
            BigDecimal amount = (BigDecimal) result[1];

            if (type == TransactionType.INCOME) {
                periodIncome = amount;
            } else if (type == TransactionType.EXPENSE) {
                periodExpenses = amount;
            }
        }

        // Get current total balance and savings
        FinancialOverview current = getFinancialOverview(userId);

        return FinancialOverview.builder()
                .totalBalance(current.getTotalBalance())
                .monthlyIncome(periodIncome)
                .monthlyExpenses(periodExpenses)
                .totalSavings(current.getTotalSavings())
                .netIncome(periodIncome.subtract(periodExpenses))
                .build();
    }

    @Override
    public SavingsAnalytics getSavingsAnalytics(Long userId) {
        log.debug("Getting savings analytics for user: {}", userId);

        String analyticsQuery = "SELECT " +
                "COUNT(sg), " +
                "SUM(sg.targetAmount), " +
                "SUM(sg.currentAmount), " +
                "AVG(sg.currentAmount / sg.targetAmount * 100), " +
                "COUNT(CASE WHEN sg.status = 'COMPLETED' THEN 1 END), " +
                "COUNT(CASE WHEN sg.status = 'ACTIVE' THEN 1 END) " +
                "FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId";

        Object[] result = (Object[]) entityManager.createQuery(analyticsQuery)
                .setParameter("userId", userId)
                .getSingleResult();

        return SavingsAnalytics.builder()
                .totalGoals(((Long) result[0]).intValue())
                .totalTargetAmount((BigDecimal) result[1])
                .totalCurrentAmount((BigDecimal) result[2])
                .averageProgress(result[3] != null ? BigDecimal.valueOf((Double) result[3]) : BigDecimal.ZERO)
                .completedGoals(((Long) result[4]).intValue())
                .activeGoals(((Long) result[5]).intValue())
                .build();
    }

    @Override
    public SpendingAnalytics getSpendingAnalytics(Long userId) {
        log.debug("Getting spending analytics for user: {}", userId);

        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);

        String spendingQuery = "SELECT " +
                "COUNT(t), " +
                "SUM(t.amount), " +
                "AVG(t.amount), " +
                "MAX(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionType = 'EXPENSE' " +
                "AND t.transactionDate >= :startOfMonth";

        Object[] result = (Object[]) entityManager.createQuery(spendingQuery)
                .setParameter("userId", userId)
                .setParameter("startOfMonth", startOfMonth)
                .getSingleResult();

        return SpendingAnalytics.builder()
                .totalTransactions(((Long) result[0]).intValue())
                .totalSpent(result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO)
                .averageTransaction(result[2] != null ? BigDecimal.valueOf(((Number) result[2]).doubleValue()) : BigDecimal.ZERO)
                .largestTransaction(result[3] != null ? BigDecimal.valueOf(((Number) result[3]).doubleValue()) : BigDecimal.ZERO)
                .topCategories(getTopSpendingCategories(userId, startOfMonth))
                .build();
    }

    private List<CategorySpending> getTopSpendingCategories(Long userId, LocalDateTime startOfMonth) {
        String categoryQuery = "SELECT t.category, SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionType = 'EXPENSE' " +
                "AND t.transactionDate >= :startOfMonth " +
                "AND t.category IS NOT NULL " +
                "GROUP BY t.category " +
                "ORDER BY SUM(t.amount) DESC";

        List<Object[]> categoryResults = entityManager.createQuery(categoryQuery)
                .setParameter("userId", userId)
                .setParameter("startOfMonth", startOfMonth)
                .setMaxResults(5)
                .getResultList();

        return categoryResults.stream()
                .map(r -> CategorySpending.builder()
                        .category((String) r[0])
                        .amount((BigDecimal) r[1])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<MonthlyTrend> getMonthlyTrends(Long userId, int monthsBack) {
        log.debug("Getting monthly trends for user: {} for {} months", userId, monthsBack);

        String trendQuery = "SELECT " +
                "YEAR(t.transactionDate), " +
                "MONTH(t.transactionDate), " +
                "t.transactionType, " +
                "SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate >= :startDate " +
                "GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate), t.transactionType " +
                "ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)";

        LocalDateTime startDate = LocalDateTime.now().minusMonths(monthsBack);

        List<Object[]> results = entityManager.createQuery(trendQuery)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .getResultList();

        return results.stream()
                .map(result -> MonthlyTrend.builder()
                        .year((Integer) result[0])
                        .month((Integer) result[1])
                        .transactionType((TransactionType) result[2])
                        .amount((BigDecimal) result[3])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionDto> getTransactionsForPeriod(Long userId, LocalDateTime startDate,
                                                         LocalDateTime endDate, int limit) {
        log.debug("Getting transactions for user: {} for period {} to {}", userId, startDate, endDate);

        String jpql = "SELECT t FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY t.transactionDate DESC";

        TypedQuery<Transaction> query = entityManager.createQuery(jpql, Transaction.class);
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(limit);

        List<Transaction> transactions = query.getResultList();

        return transactions.stream()
                .map(this::convertToTransactionDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DashboardAlert> getDashboardAlerts(Long userId) {
        log.debug("Getting dashboard alerts for user: {}", userId);

        List<DashboardAlert> alerts = new ArrayList<>();

        // Check for low balance accounts
        String lowBalanceQuery = "SELECT a.id, a.accountNumber, a.balance " +
                "FROM Account a " +
                "WHERE a.user.id = :userId " +
                "AND a.balance < :threshold " +
                "AND a.balance > 0";

        List<Object[]> lowBalanceResults = entityManager.createQuery(lowBalanceQuery)
                .setParameter("userId", userId)
                .setParameter("threshold", BigDecimal.valueOf(1000))
                .getResultList();

        for (Object[] result : lowBalanceResults) {
            alerts.add(DashboardAlert.builder()
                    .type(AlertType.LOW_BALANCE)
                    .title("Low Account Balance")
                    .message("Account " + result[1] + " has low balance: " + result[2])
                    .severity(AlertSeverity.WARNING)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        // Check for overdue goals
        String overdueGoalsQuery = "SELECT sg.id, sg.name, sg.targetDate " +
                "FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId " +
                "AND sg.status = 'ACTIVE' " +
                "AND sg.targetDate < CURRENT_DATE " +
                "AND sg.currentAmount < sg.targetAmount";

        List<Object[]> overdueResults = entityManager.createQuery(overdueGoalsQuery)
                .setParameter("userId", userId)
                .getResultList();

        for (Object[] result : overdueResults) {
            alerts.add(DashboardAlert.builder()
                    .type(AlertType.OVERDUE_GOAL)
                    .title("Overdue Savings Goal")
                    .message("Goal '" + result[1] + "' was due on " + result[2])
                    .severity(AlertSeverity.ERROR)
                    .createdAt(LocalDateTime.now())
                    .build());
        }

        return alerts;
    }

    @Override
    public List<BalanceHistory> getBalanceHistory(Long userId, int daysBack) {
        log.debug("Getting balance history for user: {} for {} days", userId, daysBack);

        // This would typically require a separate balance_history table
        // For now, we'll simulate it with transaction data
        LocalDateTime startDate = LocalDateTime.now().minusDays(daysBack);

        String historyQuery = "SELECT DATE(t.transactionDate), SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate >= :startDate " +
                "GROUP BY DATE(t.transactionDate) " +
                "ORDER BY DATE(t.transactionDate)";

        List<Object[]> results = entityManager.createQuery(historyQuery)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .getResultList();

        return results.stream()
                .map(result -> BalanceHistory.builder()
                        .date(((java.sql.Date) result[0]).toLocalDate())
                        .balance((BigDecimal) result[1])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryTrend> getCategoryTrends(Long userId, int monthsBack) {
        log.debug("Getting category trends for user: {} for {} months", userId, monthsBack);

        LocalDateTime startDate = LocalDateTime.now().minusMonths(monthsBack);

        String trendQuery = "SELECT " +
                "t.category, " +
                "YEAR(t.transactionDate), " +
                "MONTH(t.transactionDate), " +
                "SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionType = 'EXPENSE' " +
                "AND t.transactionDate >= :startDate " +
                "AND t.category IS NOT NULL " +
                "GROUP BY t.category, YEAR(t.transactionDate), MONTH(t.transactionDate) " +
                "ORDER BY t.category, YEAR(t.transactionDate), MONTH(t.transactionDate)";

        List<Object[]> results = entityManager.createQuery(trendQuery)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .getResultList();

        return results.stream()
                .map(result -> CategoryTrend.builder()
                        .category((String) result[0])
                        .year((Integer) result[1])
                        .month((Integer) result[2])
                        .amount((BigDecimal) result[3])
                        .build())
                .collect(Collectors.toList());
    }

    private TransactionDto convertToTransactionDto(Transaction transaction) {
        return new TransactionDto(
                transaction.getId(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getTransactionType().name(), // Convert enum to String
                transaction.getCurrency().name(),       // Convert enum to String
                transaction.getAccount().getId(),
                transaction.getUser().getId(),
                transaction.getAccount().getAccountNumber(),
                transaction.getCategory(),
                transaction.getCreatedAt()
        );
    }

    // Helper classes and enums
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class FinancialOverview {
        private BigDecimal totalBalance;
        private BigDecimal monthlyIncome;
        private BigDecimal monthlyExpenses;
        private BigDecimal totalSavings;
        private BigDecimal netIncome;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SavingsAnalytics {
        private int totalGoals;
        private BigDecimal totalTargetAmount;
        private BigDecimal totalCurrentAmount;
        private BigDecimal averageProgress;
        private int completedGoals;
        private int activeGoals;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SpendingAnalytics {
        private int totalTransactions;
        private BigDecimal totalSpent;
        private BigDecimal averageTransaction;
        private BigDecimal largestTransaction;
        private List<CategorySpending> topCategories;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CategorySpending {
        private String category;
        private BigDecimal amount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MonthlyTrend {
        private int year;
        private int month;
        private TransactionType transactionType;
        private BigDecimal amount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class BalanceHistory {
        private LocalDate date;
        private BigDecimal balance;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CategoryTrend {
        private String category;
        private int year;
        private int month;
        private BigDecimal amount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class DashboardAlert {
        private AlertType type;
        private String title;
        private String message;
        private AlertSeverity severity;
        private LocalDateTime createdAt;
    }

    public enum AlertType {
        LOW_BALANCE,
        OVERDUE_GOAL,
        GOAL_COMPLETED,
        HIGH_SPENDING,
        REWARD_AVAILABLE
    }

    public enum AlertSeverity {
        INFO,
        WARNING,
        ERROR,
        SUCCESS
    }
}
