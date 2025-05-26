package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.model.entity.SavingsGoal;
import no.stacc.payforjoy.enums.GoalStatus;
import no.stacc.payforjoy.enums.Currency;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@Slf4j
public class SavingsGoalRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find goals nearing their target date
     */
    public List<SavingsGoal> findGoalsNearingDeadline(Long userId, int daysFromNow) {
        log.debug("Finding goals nearing deadline for user: {} within {} days", userId, daysFromNow);

        LocalDate targetDate = LocalDate.now().plusDays(daysFromNow);

        String jpql = "SELECT sg FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId " +
                "AND sg.status = 'ACTIVE' " +
                "AND sg.targetDate <= :targetDate " +
                "AND sg.targetDate >= CURRENT_DATE " +
                "ORDER BY sg.targetDate ASC";

        TypedQuery<SavingsGoal> query = entityManager.createQuery(jpql, SavingsGoal.class);
        query.setParameter("userId", userId);
        query.setParameter("targetDate", targetDate);

        List<SavingsGoal> results = query.getResultList();
        log.debug("Found {} goals nearing deadline", results.size());
        return results;
    }

    /**
     * Find goals by progress percentage range
     */
    public List<SavingsGoal> findGoalsByProgressRange(Long userId, BigDecimal minProgress, BigDecimal maxProgress) {
        log.debug("Finding goals with progress between {}% and {}% for user: {}", minProgress, maxProgress, userId);

        String jpql = "SELECT sg FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId " +
                "AND sg.status = 'ACTIVE' " +
                "AND (sg.currentAmount / sg.targetAmount * 100) BETWEEN :minProgress AND :maxProgress " +
                "ORDER BY (sg.currentAmount / sg.targetAmount) DESC";

        TypedQuery<SavingsGoal> query = entityManager.createQuery(jpql, SavingsGoal.class);
        query.setParameter("userId", userId);
        query.setParameter("minProgress", minProgress);
        query.setParameter("maxProgress", maxProgress);

        List<SavingsGoal> results = query.getResultList();
        log.debug("Found {} goals in progress range", results.size());
        return results;
    }

    /**
     * Find goals with complex criteria
     */
    public List<SavingsGoal> findGoalsWithCriteria(Long userId, GoalStatus status, Currency currency,
                                                   BigDecimal minAmount, BigDecimal maxAmount,
                                                   LocalDate startDate, LocalDate endDate) {
        log.debug("Finding goals with criteria for user: {}", userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SavingsGoal> query = cb.createQuery(SavingsGoal.class);
        Root<SavingsGoal> goal = query.from(SavingsGoal.class);

        List<Predicate> predicates = new ArrayList<>();

        // Always filter by user ID
        predicates.add(cb.equal(goal.get("user").get("id"), userId));

        // Optional filters
        if (status != null) {
            predicates.add(cb.equal(goal.get("status"), status));
        }

        if (currency != null) {
            predicates.add(cb.equal(goal.get("currency"), currency));
        }

        if (minAmount != null) {
            predicates.add(cb.greaterThanOrEqualTo(goal.get("targetAmount"), minAmount));
        }

        if (maxAmount != null) {
            predicates.add(cb.lessThanOrEqualTo(goal.get("targetAmount"), maxAmount));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(goal.get("targetDate"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(goal.get("targetDate"), endDate));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(goal.get("createdAt")));

        TypedQuery<SavingsGoal> typedQuery = entityManager.createQuery(query);
        List<SavingsGoal> results = typedQuery.getResultList();

        log.debug("Found {} goals matching criteria", results.size());
        return results;
    }

    /**
     * Get savings goal statistics for a user
     */
    public SavingsGoalStatistics getSavingsGoalStatistics(Long userId) {
        log.debug("Calculating savings goal statistics for user: {}", userId);

        String jpql = "SELECT " +
                "COUNT(sg), " +
                "SUM(sg.targetAmount), " +
                "SUM(sg.currentAmount), " +
                "AVG(sg.currentAmount / sg.targetAmount * 100), " +
                "COUNT(CASE WHEN sg.status = 'COMPLETED' THEN 1 END), " +
                "COUNT(CASE WHEN sg.status = 'ACTIVE' THEN 1 END) " +
                "FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId";

        Object[] result = (Object[]) entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .getSingleResult();

        SavingsGoalStatistics stats = SavingsGoalStatistics.builder()
                .totalGoals(((Long) result[0]).intValue())
                .totalTargetAmount((BigDecimal) result[1])
                .totalCurrentAmount((BigDecimal) result[2])
                .averageProgress((BigDecimal) result[3])
                .completedGoals(((Long) result[4]).intValue())
                .activeGoals(((Long) result[5]).intValue())
                .build();

        log.debug("Savings goal statistics calculated: {}", stats);
        return stats;
    }

    /**
     * Find overdue goals
     */
    public List<SavingsGoal> findOverdueGoals(Long userId) {
        log.debug("Finding overdue goals for user: {}", userId);

        String jpql = "SELECT sg FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId " +
                "AND sg.status = 'ACTIVE' " +
                "AND sg.targetDate < CURRENT_DATE " +
                "AND sg.currentAmount < sg.targetAmount " +
                "ORDER BY sg.targetDate ASC";

        TypedQuery<SavingsGoal> query = entityManager.createQuery(jpql, SavingsGoal.class);
        query.setParameter("userId", userId);

        List<SavingsGoal> results = query.getResultList();
        log.debug("Found {} overdue goals", results.size());
        return results;
    }

    /**
     * Find goals that can be completed soon
     */
    public List<SavingsGoal> findGoalsNearCompletion(Long userId, BigDecimal progressThreshold) {
        log.debug("Finding goals near completion for user: {} with progress >= {}%", userId, progressThreshold);

        String jpql = "SELECT sg FROM SavingsGoal sg " +
                "WHERE sg.user.id = :userId " +
                "AND sg.status = 'ACTIVE' " +
                "AND (sg.currentAmount / sg.targetAmount * 100) >= :progressThreshold " +
                "ORDER BY (sg.currentAmount / sg.targetAmount) DESC";

        TypedQuery<SavingsGoal> query = entityManager.createQuery(jpql, SavingsGoal.class);
        query.setParameter("userId", userId);
        query.setParameter("progressThreshold", progressThreshold);

        List<SavingsGoal> results = query.getResultList();
        log.debug("Found {} goals near completion", results.size());
        return results;
    }

    /**
     * Update progress for multiple goals
     */
    public int updateGoalsProgress(List<Long> goalIds) {
        log.info("Updating progress calculation for {} goals", goalIds.size());

        String jpql = "UPDATE SavingsGoal sg " +
                "SET sg.updatedAt = :now " +
                "WHERE sg.id IN :goalIds";

        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("now", LocalDateTime.now())
                .setParameter("goalIds", goalIds)
                .executeUpdate();

        log.info("Updated {} goals", updatedCount);
        return updatedCount;
    }

    /**
     * Helper class for savings goal statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class SavingsGoalStatistics {
        private int totalGoals;
        private BigDecimal totalTargetAmount;
        private BigDecimal totalCurrentAmount;
        private BigDecimal averageProgress;
        private int completedGoals;
        private int activeGoals;
    }
}
