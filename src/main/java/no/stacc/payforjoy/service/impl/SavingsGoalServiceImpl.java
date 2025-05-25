package no.stacc.payforjoy.service.impl;

import no.stacc.payforjoy.interfaces.service.SavingsGoalService;
import no.stacc.payforjoy.interfaces.repository.SavingsGoalRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.model.dto.SavingsGoalDto;
import no.stacc.payforjoy.model.entity.SavingsGoal;
import no.stacc.payforjoy.model.entity.User;
import no.stacc.payforjoy.enums.GoalStatus;
import no.stacc.payforjoy.exception.custom.GoalNotFoundException;
import no.stacc.payforjoy.logging.PayForJoyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SavingsGoalServiceImpl implements SavingsGoalService {

    private final SavingsGoalRepository goalRepository;
    private final UserRepository userRepository;
    private final PayForJoyLogger logger;

    @Autowired
    public SavingsGoalServiceImpl(SavingsGoalRepository goalRepository,
                                  UserRepository userRepository,
                                  PayForJoyLogger logger) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
        this.logger = logger;
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoalDto> findAllByUserId(Long userId) {
        logger.info("Fetching savings goals for user: {}", userId);
        List<SavingsGoal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return goals.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoalDto> findActiveGoalsByUserId(Long userId) {
        List<SavingsGoal> goals = goalRepository.findByUserIdAndStatus(userId, GoalStatus.ACTIVE);
        return goals.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public SavingsGoalDto createGoal(SavingsGoalDto goalDto, Long userId) {
        logger.info("Creating savings goal for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SavingsGoal goal = convertToEntity(goalDto);
        goal.setUser(user);
        goal.setStatus(GoalStatus.ACTIVE);

        SavingsGoal savedGoal = goalRepository.save(goal);
        logger.info("Savings goal created with ID: {}", savedGoal.getId());

        return convertToDto(savedGoal);
    }

    @Override
    public SavingsGoalDto addSavings(Long goalId, BigDecimal amount) {
        logger.info("Adding {} to savings goal: {}", amount, goalId);

        SavingsGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Savings goal not found"));

        BigDecimal newAmount = goal.getCurrentAmount().add(amount);
        goal.setCurrentAmount(newAmount);

        // Check if goal is completed
        if (newAmount.compareTo(goal.getTargetAmount()) >= 0) {
            goal.setStatus(GoalStatus.COMPLETED);
            logger.info("Savings goal completed: {}", goalId);
        }

        SavingsGoal updatedGoal = goalRepository.save(goal);
        return convertToDto(updatedGoal);
    }

    @Override
    public long countActiveGoals(Long userId) {
        return goalRepository.countByUserIdAndStatus(userId, GoalStatus.ACTIVE);
    }

    @Override
    public long countCompletedGoals(Long userId) {
        return goalRepository.countByUserIdAndStatus(userId, GoalStatus.COMPLETED);
    }

    private SavingsGoalDto convertToDto(SavingsGoal goal) {
        return new SavingsGoalDto(
                goal.getId(),
                goal.getName(),
                goal.getDescription(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate(),
                goal.getStatus(),
                goal.getCurrency(),
                goal.getProgressPercentage(),
                goal.getUser().getId(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

    private SavingsGoal convertToEntity(SavingsGoalDto dto) {
        SavingsGoal goal = new SavingsGoal();
        goal.setName(dto.getName());
        goal.setDescription(dto.getDescription());
        goal.setTargetAmount(dto.getTargetAmount());
        goal.setCurrentAmount(dto.getCurrentAmount() != null ? dto.getCurrentAmount() : BigDecimal.ZERO);
        goal.setTargetDate(dto.getTargetDate());
        goal.setCurrency(dto.getCurrency());
        return goal;
    }

    @Override
    @Transactional(readOnly = true)
    public SavingsGoalDto findById(Long id) {
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        return convertToDto(goal);
    }

    @Override
    public SavingsGoalDto updateGoal(Long id, SavingsGoalDto goalDto) {
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found"));

        goal.setName(goalDto.getName());
        goal.setDescription(goalDto.getDescription());
        goal.setTargetAmount(goalDto.getTargetAmount());
        goal.setTargetDate(goalDto.getTargetDate());

        SavingsGoal updated = goalRepository.save(goal);
        return convertToDto(updated);
    }

    @Override
    public void deleteGoal(Long id) {
        SavingsGoal goal = goalRepository.findById(id)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        goalRepository.delete(goal);
        logger.info("Savings goal deleted: {}", id);
    }

    @Override
    public SavingsGoalDto updateGoalStatus(Long goalId, GoalStatus status) {
        SavingsGoal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found"));
        goal.setStatus(status);
        SavingsGoal updated = goalRepository.save(goal);
        return convertToDto(updated);
    }
}


