package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.model.entity.Reward;
import no.stacc.payforjoy.enums.RewardType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Transactional
@Slf4j
public class RewardRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find rewards with complex criteria
     */
    public List<Reward> findRewardsWithCriteria(Long userId, RewardType rewardType, Boolean isClaimed,
                                                LocalDateTime startDate, LocalDateTime endDate,
                                                Integer minPoints, Integer maxPoints) {
        log.debug("Finding rewards with criteria for user: {}", userId);

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Reward> query = cb.createQuery(Reward.class);
        Root<Reward> reward = query.from(Reward.class);

        List<Predicate> predicates = new ArrayList<>();

        // Always filter by user ID
        predicates.add(cb.equal(reward.get("user").get("id"), userId));

        // Optional filters
        if (rewardType != null) {
            predicates.add(cb.equal(reward.get("rewardType"), rewardType));
        }

        if (isClaimed != null) {
            predicates.add(cb.equal(reward.get("isClaimed"), isClaimed));
        }

        if (startDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(reward.get("earnedAt"), startDate));
        }

        if (endDate != null) {
            predicates.add(cb.lessThanOrEqualTo(reward.get("earnedAt"), endDate));
        }

        if (minPoints != null) {
            predicates.add(cb.greaterThanOrEqualTo(reward.get("points"), minPoints));
        }

        if (maxPoints != null) {
            predicates.add(cb.lessThanOrEqualTo(reward.get("points"), maxPoints));
        }

        query.where(predicates.toArray(new Predicate[0]));
        query.orderBy(cb.desc(reward.get("earnedAt")));

        TypedQuery<Reward> typedQuery = entityManager.createQuery(query);
        List<Reward> results = typedQuery.getResultList();

        log.debug("Found {} rewards matching criteria", results.size());
        return results;
    }

    /**
     * Get rewards summary by type
     */
    public List<RewardTypeSummary> getRewardsSummaryByType(Long userId) {
        log.debug("Calculating rewards summary by type for user: {}", userId);

        String jpql = "SELECT r.rewardType, COUNT(r), SUM(r.points), " +
                "COUNT(CASE WHEN r.isClaimed = true THEN 1 END) " +
                "FROM Reward r " +
                "WHERE r.user.id = :userId " +
                "GROUP BY r.rewardType " +
                "ORDER BY SUM(r.points) DESC";

        List<Object[]> results = entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .getResultList();

        List<RewardTypeSummary> summaries = results.stream()
                .map(result -> RewardTypeSummary.builder()
                        .rewardType((RewardType) result[0])
                        .totalRewards(((Long) result[1]).intValue())
                        .totalPoints(((Long) result[2]).intValue())
                        .claimedRewards(((Long) result[3]).intValue())
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} reward type summaries", summaries.size());
        return summaries;
    }

    /**
     * Find recent unclaimed rewards
     */
    public List<Reward> findRecentUnclaimedRewards(Long userId, int daysBack) {
        log.debug("Finding recent unclaimed rewards for user: {} within {} days", userId, daysBack);

        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysBack);

        String jpql = "SELECT r FROM Reward r " +
                "WHERE r.user.id = :userId " +
                "AND r.isClaimed = false " +
                "AND r.earnedAt >= :cutoffDate " +
                "ORDER BY r.earnedAt DESC";

        TypedQuery<Reward> query = entityManager.createQuery(jpql, Reward.class);
        query.setParameter("userId", userId);
        query.setParameter("cutoffDate", cutoffDate);

        List<Reward> results = query.getResultList();
        log.debug("Found {} recent unclaimed rewards", results.size());
        return results;
    }

    /**
     * Get monthly reward statistics
     */
    public List<MonthlyRewardSummary> getMonthlyRewardStatistics(Long userId, int year) {
        log.debug("Calculating monthly reward statistics for user: {} in year: {}", userId, year);

        String jpql = "SELECT " +
                "MONTH(r.earnedAt), " +
                "COUNT(r), " +
                "SUM(r.points), " +
                "COUNT(CASE WHEN r.isClaimed = true THEN 1 END) " +
                "FROM Reward r " +
                "WHERE r.user.id = :userId " +
                "AND YEAR(r.earnedAt) = :year " +
                "GROUP BY MONTH(r.earnedAt) " +
                "ORDER BY MONTH(r.earnedAt)";

        List<Object[]> results = entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .setParameter("year", year)
                .getResultList();

        List<MonthlyRewardSummary> summaries = results.stream()
                .map(result -> MonthlyRewardSummary.builder()
                        .month((Integer) result[0])
                        .totalRewards(((Long) result[1]).intValue())
                        .totalPoints(((Long) result[2]).intValue())
                        .claimedRewards(((Long) result[3]).intValue())
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} monthly reward summaries", summaries.size());
        return summaries;
    }

    /**
     * Find top performing users by reward points
     */
    public List<UserRewardSummary> findTopUsersByPoints(int limit) {
        log.debug("Finding top {} users by reward points", limit);

        String jpql = "SELECT r.user.id, r.user.firstName, r.user.lastName, " +
                "SUM(r.points), COUNT(r) " +
                "FROM Reward r " +
                "GROUP BY r.user.id, r.user.firstName, r.user.lastName " +
                "ORDER BY SUM(r.points) DESC";

        List<Object[]> results = entityManager.createQuery(jpql)
                .setMaxResults(limit)
                .getResultList();

        List<UserRewardSummary> summaries = results.stream()
                .map(result -> UserRewardSummary.builder()
                        .userId((Long) result[0])
                        .firstName((String) result[1])
                        .lastName((String) result[2])
                        .totalPoints(((Long) result[3]).intValue())
                        .totalRewards(((Long) result[4]).intValue())
                        .build())
                .collect(Collectors.toList());

        log.debug("Found {} top users by points", summaries.size());
        return summaries;
    }

    /**
     * Bulk claim rewards
     */
    public int claimMultipleRewards(List<Long> rewardIds, Long userId) {
        log.info("Bulk claiming {} rewards for user: {}", rewardIds.size(), userId);

        String jpql = "UPDATE Reward r " +
                "SET r.isClaimed = true, r.claimedAt = :now " +
                "WHERE r.id IN :rewardIds " +
                "AND r.user.id = :userId " +
                "AND r.isClaimed = false";

        int updatedCount = entityManager.createQuery(jpql)
                .setParameter("now", LocalDateTime.now())
                .setParameter("rewardIds", rewardIds)
                .setParameter("userId", userId)
                .executeUpdate();

        log.info("Claimed {} rewards for user: {}", updatedCount, userId);
        return updatedCount;
    }

    /**
     * Get reward statistics for a user
     */
    public RewardStatistics getRewardStatistics(Long userId) {
        log.debug("Calculating reward statistics for user: {}", userId);

        String jpql = "SELECT " +
                "COUNT(r), " +
                "SUM(r.points), " +
                "COUNT(CASE WHEN r.isClaimed = true THEN 1 END), " +
                "SUM(CASE WHEN r.isClaimed = true THEN r.points ELSE 0 END), " +
                "MAX(r.points), " +
                "AVG(r.points) " +
                "FROM Reward r " +
                "WHERE r.user.id = :userId";

        Object[] result = (Object[]) entityManager.createQuery(jpql)
                .setParameter("userId", userId)
                .getSingleResult();

        RewardStatistics stats = RewardStatistics.builder()
                .totalRewards(((Long) result[0]).intValue())
                .totalPoints(result[1] != null ? ((Long) result[1]).intValue() : 0)
                .claimedRewards(((Long) result[2]).intValue())
                .claimedPoints(result[3] != null ? ((Long) result[3]).intValue() : 0)
                .highestReward(result[4] != null ? (Integer) result[4] : 0)
                .averagePoints(result[5] != null ? ((Double) result[5]).intValue() : 0)
                .build();

        log.debug("Reward statistics calculated: {}", stats);
        return stats;
    }

    // Helper classes
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RewardTypeSummary {
        private RewardType rewardType;
        private int totalRewards;
        private int totalPoints;
        private int claimedRewards;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MonthlyRewardSummary {
        private int month;
        private int totalRewards;
        private int totalPoints;
        private int claimedRewards;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserRewardSummary {
        private Long userId;
        private String firstName;
        private String lastName;
        private int totalPoints;
        private int totalRewards;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RewardStatistics {
        private int totalRewards;
        private int totalPoints;
        private int claimedRewards;
        private int claimedPoints;
        private int highestReward;
        private int averagePoints;
    }
}
