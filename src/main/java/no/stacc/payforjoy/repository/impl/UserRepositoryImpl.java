package no.stacc.payforjoy.repository.impl;

import no.stacc.payforjoy.model.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
@Slf4j
public class UserRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find users by age range
     */
    public List<User> findUsersByAgeRange(int minAge, int maxAge) {
        log.debug("Finding users between ages {} and {}", minAge, maxAge);

        LocalDate maxBirthDate = LocalDate.now().minusYears(minAge);
        LocalDate minBirthDate = LocalDate.now().minusYears(maxAge + 1);

        String jpql = "SELECT u FROM User u WHERE u.dateOfBirth BETWEEN :minBirthDate AND :maxBirthDate " +
                "ORDER BY u.dateOfBirth DESC";

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("minBirthDate", minBirthDate);
        query.setParameter("maxBirthDate", maxBirthDate);

        List<User> results = query.getResultList();
        log.debug("Found {} users in age range", results.size());
        return results;
    }

    /**
     * Find users created within a specific time period
     */
    public List<User> findUsersCreatedBetween(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding users created between {} and {}", startDate, endDate);

        String jpql = "SELECT u FROM User u WHERE u.createdAt BETWEEN :startDate AND :endDate " +
                "ORDER BY u.createdAt DESC";

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);

        List<User> results = query.getResultList();
        log.debug("Found {} users created in date range", results.size());
        return results;
    }

    /**
     * Search users by name (first name or last name)
     */
    public List<User> searchUsersByName(String searchTerm) {
        log.debug("Searching users by name: {}", searchTerm);

        String jpql = "SELECT u FROM User u WHERE " +
                "LOWER(u.firstName) LIKE LOWER(:searchTerm) OR " +
                "LOWER(u.lastName) LIKE LOWER(:searchTerm) OR " +
                "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(:searchTerm) " +
                "ORDER BY u.firstName, u.lastName";

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        query.setParameter("searchTerm", "%" + searchTerm + "%");

        List<User> results = query.getResultList();
        log.debug("Found {} users matching search term", results.size());
        return results;
    }

    /**
     * Find users with multiple accounts
     */
    public List<User> findUsersWithMultipleAccounts() {
        log.debug("Finding users with multiple accounts");

        String jpql = "SELECT u FROM User u WHERE SIZE(u.accounts) > 1 " +
                "ORDER BY SIZE(u.accounts) DESC";

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        List<User> results = query.getResultList();

        log.debug("Found {} users with multiple accounts", results.size());
        return results;
    }

    /**
     * Find users with active savings goals
     */
    public List<User> findUsersWithActiveSavingsGoals() {
        log.debug("Finding users with active savings goals");

        String jpql = "SELECT DISTINCT u FROM User u JOIN u.savingsGoals sg " +
                "WHERE sg.status = 'ACTIVE' " +
                "ORDER BY u.firstName, u.lastName";

        TypedQuery<User> query = entityManager.createQuery(jpql, User.class);
        List<User> results = query.getResultList();

        log.debug("Found {} users with active savings goals", results.size());
        return results;
    }

    /**
     * Get user statistics
     */
    public UserStatistics getUserStatistics() {
        log.debug("Calculating user statistics");

        String jpql = "SELECT " +
                "COUNT(u), " +
                "AVG(YEAR(CURRENT_DATE) - YEAR(u.dateOfBirth)), " +
                "COUNT(CASE WHEN u.phone IS NOT NULL THEN 1 END) " +
                "FROM User u";

        Object[] result = (Object[]) entityManager.createQuery(jpql).getSingleResult();

        UserStatistics stats = UserStatistics.builder()
                .totalUsers(((Long) result[0]).intValue())
                .averageAge(result[1] != null ? ((Double) result[1]).intValue() : 0)
                .usersWithPhone(((Long) result[2]).intValue())
                .build();

        log.debug("User statistics calculated: {}", stats);
        return stats;
    }

    /**
     * Helper class for user statistics
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class UserStatistics {
        private int totalUsers;
        private int averageAge;
        private int usersWithPhone;
    }
}
