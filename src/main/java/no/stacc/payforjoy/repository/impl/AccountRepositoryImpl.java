package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.enums.AccountType;
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

@Repository
@Transactional
@Slf4j
public class AccountRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Custom method to find accounts with complex criteria
     */
    public List<Account> findAccountsWithCriteria(Long userId, AccountType accountType,
                                                  BigDecimal minBalance, Currency currency) {
        log.debug("Finding accounts with criteria for user: {}", userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Account> query = cb.createQuery(Account.class);
        Root<Account> account = query.from(Account.class);

        List<Predicate> predicates = new ArrayList<>();

        // Always filter by user ID
        predicates.add(cb.equal(account.get("user").get("id"), userId));

        // Optional filters
        if (accountType != null) {
            predicates.add(cb.equal(account.get("accountType"), accountType));
        }

        if (minBalance != null) {
            predicates.add(cb.greaterThanOrEqualTo(account.get("balance"), minBalance));
        }

        if (currency != null) {
            predicates.add(cb.equal(account.get("currency"), currency));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(account.get("balance")));

        TypedQuery<Account> typedQuery = entityManager.createQuery(query);
        List<Account> results = typedQuery.getResultList();

        log.debug("Found {} accounts matching criteria", results.size());
        return results;
    }

    /**
     * Custom method to find accounts created within a date range
     */
    public List<Account> findAccountsCreatedBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding accounts created between {} and {} for user: {}", startDate, endDate, userId);

        String jpql = "SELECT a FROM Account a WHERE a.user.id = :userId " +
                "AND a.createdAt BETWEEN :startDate AND :endDate " +
                "ORDER BY a.createdAt DESC";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class);
        query.setParameter("userId", userId);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<Account> results = query.getResultList();
        log.debug("Found {} accounts created in date range", results.size());
        return results;
    }

    /**
     * Custom method to update account balances in bulk
     */
    public int updateAccountBalances(List<Long> accountIds, BigDecimal adjustmentAmount) {
        log.info("Bulk updating {} account balances with adjustment: {}", accountIds.size(), adjustmentAmount);

        String jpql = "UPDATE Account a SET a.balance = a.balance + :adjustment, a.updatedAt = :now " +
                "WHERE a.id IN :accountIds";

        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("adjustment", adjustmentAmount)
                .setParameter("now", LocalDateTime.now())
                .setParameter("accountIds", accountIds)
                .executeUpdate();

        log.info("Updated {} account balances", updatedCount);
        return updatedCount;
    }

    /**
     * Custom method to find accounts with low balances
     */
    public List<Account> findLowBalanceAccounts(Long userId, BigDecimal threshold) {
        log.debug("Finding low balance accounts for user: {} with threshold: {}", userId, threshold);

        String jpql = "SELECT a FROM Account a WHERE a.user.id = :userId " +
                "AND a.balance < :threshold AND a.balance > 0 " +
                "ORDER BY a.balance ASC";

        TypedQuery<Account> query = entityManager.createQuery(jpql, Account.class);
        query.setParameter("userId", userId);
        query.setParameter("threshold", threshold);

        List<Account> results = query.getResultList();
        log.debug("Found {} low balance accounts", results.size());
        return results;
    }

    /**
     * Custom method to get account statistics
     */
    public AccountStatistics getAccountStatistics(Long userId) {
        log.debug("Calculating account statistics for user: {}", userId);

        String jpql = "SELECT " +
                "COUNT(a), " +
                "SUM(a.balance), " +
                "AVG(a.balance), " +
                "MAX(a.balance), " +
                "MIN(a.balance) " +
                "FROM Account a WHERE a.user.id = :userId";

        Object[] result = (Object[]) entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .getSingleResult();

        AccountStatistics stats = AccountStatistics.builder()
                .totalAccounts(((Long) result[0]).intValue())
                .totalBalance((BigDecimal) result[1])
                .averageBalance((BigDecimal) result[2])
                .maxBalance((BigDecimal) result[3])
                .minBalance((BigDecimal) result[4])
                .build();

        log.debug("Account statistics calculated: {}", stats);
        return stats;
    }

    /**
     * Helper class for account statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AccountStatistics {
        private int totalAccounts;
        private BigDecimal totalBalance;
        private BigDecimal averageBalance;
        private BigDecimal maxBalance;
        private BigDecimal minBalance;
    }
}
