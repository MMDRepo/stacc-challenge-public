package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.model.entity.Transaction;
import no.stacc.payforjoy.enums.TransactionType;
import no.stacc.payforjoy.enums.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Transactional
@Slf4j
public class TransactionRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find transactions with complex criteria
     */
    public List<Transaction> findTransactionsWithCriteria(Long userId, TransactionType transactionType,
                                                          BigDecimal minAmount, BigDecimal maxAmount,
                                                          LocalDateTime startDate, LocalDateTime endDate,
                                                          String category) {
        log.debug("Finding transactions with criteria for user: {}", userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Transaction> query = cb.createQuery(Transaction.class);
        Root<Transaction> transaction = query.from(Transaction.class);
        Join<Object, Object> account = transaction.join("account");

        List<Predicate> predicates = new ArrayList<>();

        // Always filter by user ID
        predicates.add(cb.equal(account.get("user").get("id"), userId));

        // Optional filters
        if (transactionType != null) {
            predicates.add(cb.equal(transaction.get("transactionType"), transactionType));
        }

        if (minAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("amount"), minAmount));
        }

        if (maxAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(transaction.get("amount"), maxAmount));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(transaction.get("transactionDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(transaction.get("transactionDate"), endDate));
        }

        if (category != null && !category.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(transaction.get("category")),
                    "%" + category.toLowerCase() + "%"));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(transaction.get("transactionDate")));

        TypedQuery<Transaction> typedQuery = entityManager.createQuery(query);
        List<Transaction> results = typedQuery.getResultList();

        log.debug("Found {} transactions matching criteria", results.size());
        return results;
    }

    /**
     * Get spending by category for a user
     */
    public List<CategorySpending> getSpendingByCategory(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating spending by category for user: {} between {} and {}", userId, startDate, endDate);

        String jpql = "SELECT t.category, SUM(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionType = 'EXPENSE' " +
                "AND t.transactionDate BETWEEN :startDate AND :endDate " +
                "AND t.category IS NOT NULL " +
                "GROUP BY t.category " +
                "ORDER BY SUM(t.amount) DESC";

        List<Object[]> results = entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getResultList();

        List<CategorySpending> categorySpending = results.stream()
                .map(result -> CategorySpending.builder()
                        .category((String) result[0])
                        .totalAmount((BigDecimal) result[1])
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} spending categories", categorySpending.size());
        return categorySpending;
    }

    /**
     * Get monthly transaction summary
     */
    public List<MonthlyTransactionSummary> getMonthlyTransactionSummary(Long userId, int year) {
        log.debug("Calculating monthly transaction summary for user: {} in year: {}", userId, year);

        String jpql = "SELECT " +
                "MONTH(t.transactionDate), " +
                "t.transactionType, " +
                "SUM(t.amount), " +
                "COUNT(t) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND YEAR(t.transactionDate) = :year " +
                "GROUP BY MONTH(t.transactionDate), t.transactionType " +
                "ORDER BY MONTH(t.transactionDate), t.transactionType";

        List<Object[]> results = entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("year", year)
                .getResultList();

        List<MonthlyTransactionSummary> summaries = results.stream()
                .map(result -> MonthlyTransactionSummary.builder()
                        .month((Integer) result[0])
                        .transactionType((TransactionType) result[1])
                        .totalAmount((BigDecimal) result[2])
                        .transactionCount(((Long) result[3]).intValue())
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} monthly summaries", summaries.size());
        return summaries;
    }

    /**
     * Find largest transactions for a user
     */
    public List<Transaction> findLargestTransactions(Long userId, int limit) {
        log.debug("Finding {} largest transactions for user: {}", limit, userId);

        String jpql = "SELECT t FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "ORDER BY ABS(t.amount) DESC";

        TypedQuery<Transaction> query = entityManager.createQuery(jpql, Transaction.class);
        query.setParameter("userId", userId);
        query.setMaxResults(limit);

        List<Transaction> results = query.getResultList();
        log.debug("Found {} largest transactions", results.size());
        return results;
    }

    /**
     * Calculate transaction statistics for a user
     */
    public TransactionStatistics getTransactionStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Calculating transaction statistics for user: {}", userId);

        String jpql = "SELECT " +
                "COUNT(t), " +
                "SUM(CASE WHEN t.transactionType = 'INCOME' THEN t.amount ELSE 0 END), " +
                "SUM(CASE WHEN t.transactionType = 'EXPENSE' THEN t.amount ELSE 0 END), " +
                "AVG(t.amount), " +
                "MAX(t.amount), " +
                "MIN(t.amount) " +
                "FROM Transaction t JOIN t.account a " +
                "WHERE a.user.id = :userId " +
                "AND t.transactionDate BETWEEN :startDate AND :endDate";

        Object[] result = (Object[]) entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("startDate", startDate)
                .setParameter("endDate", endDate)
                .getSingleResult();

        TransactionStatistics stats = TransactionStatistics.builder()
                .totalTransactions(((Long) result[0]).intValue())
                .totalIncome((BigDecimal) result[1])
                .totalExpenses((BigDecimal) result[2])
                .averageAmount((BigDecimal) result[3])
                .maxAmount((BigDecimal) result[4])
                .minAmount((BigDecimal) result[5])
                .build();

        log.debug("Transaction statistics calculated: {}", stats);
        return stats;
    }

    // Helper classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CategorySpending {
        private String category;
        private BigDecimal totalAmount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MonthlyTransactionSummary {
        private int month;
        private TransactionType transactionType;
        private BigDecimal totalAmount;
        private int transactionCount;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class TransactionStatistics {
        private int totalTransactions;
        private BigDecimal totalIncome;
        private BigDecimal totalExpenses;
        private BigDecimal averageAmount;
        private BigDecimal maxAmount;
        private BigDecimal minAmount;
    }
}
