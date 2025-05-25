package no.stacc.payforjoy.interfaces.repository;

import no.stacc.payforjoy.model.entity.SavingsGoal;
import no.stacc.payforjoy.enums.GoalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    List<SavingsGoal> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<SavingsGoal> findByUserIdAndStatus(Long userId, GoalStatus status);
    long countByUserIdAndStatus(Long userId, GoalStatus status);
}