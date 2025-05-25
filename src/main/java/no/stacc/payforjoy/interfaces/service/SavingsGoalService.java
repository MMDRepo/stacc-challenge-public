package no.stacc.payforjoy.interfaces.service;

import no.stacc.payforjoy.model.dto.SavingsGoalDto;
import no.stacc.payforjoy.enums.GoalStatus;

import java.math.BigDecimal;
import java.util.List;

public interface SavingsGoalService {
    List<SavingsGoalDto> findAllByUserId(Long userId);
    List<SavingsGoalDto> findActiveGoalsByUserId(Long userId);
    SavingsGoalDto findById(Long id);
    SavingsGoalDto createGoal(SavingsGoalDto goalDto, Long userId);
    SavingsGoalDto updateGoal(Long id, SavingsGoalDto goalDto);
    void deleteGoal(Long id);
    SavingsGoalDto addSavings(Long goalId, BigDecimal amount);
    SavingsGoalDto updateGoalStatus(Long goalId, GoalStatus status);
    long countActiveGoals(Long userId);
    long countCompletedGoals(Long userId);
}