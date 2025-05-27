package no.stacc.payforjoy.service.impl;

import no.stacc.payforjoy.enums.GoalStatus;
import no.stacc.payforjoy.enums.RewardType;
import no.stacc.payforjoy.interfaces.repository.SavingsGoalRepository;
import no.stacc.payforjoy.interfaces.repository.TransactionRepository;
import no.stacc.payforjoy.interfaces.service.RewardService;
import no.stacc.payforjoy.interfaces.repository.RewardRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.model.dto.RewardDto;
import no.stacc.payforjoy.model.entity.Reward;
import no.stacc.payforjoy.model.entity.SavingsGoal;
import no.stacc.payforjoy.model.entity.User;
import no.stacc.payforjoy.logging.PayForJoyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class RewardServiceImpl implements RewardService {

    private final RewardRepository rewardRepository;
    private final UserRepository userRepository;
    private final PayForJoyLogger logger;
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public RewardServiceImpl(RewardRepository rewardRepository,
                             UserRepository userRepository,
                             PayForJoyLogger logger,
                             SavingsGoalRepository savingsGoalRepository,
                             TransactionRepository transactionRepository) {

        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
        this.logger = logger;
        this.savingsGoalRepository = savingsGoalRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardDto> findAllByUserId(Long userId) {
        logger.info("Fetching rewards for user: {}", userId);
        List<Reward> rewards = rewardRepository.findByUserIdOrderByEarnedAtDesc(userId);
        return rewards.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardDto> findUnclaimedByUserId(Long userId) {
        List<Reward> rewards = rewardRepository.findByUserIdAndIsClaimed(userId, false);
        return rewards.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public RewardDto createReward(RewardDto rewardDto, Long userId) {
        logger.info("Creating reward for user: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Reward reward = convertToEntity(rewardDto);
        reward.setUser(user);

        Reward savedReward = rewardRepository.save(reward);
        logger.info("Reward created with ID: {}", savedReward.getId());

        return convertToDto(savedReward);
    }

    @Override
    public RewardDto claimReward(Long id) {
        logger.info("Claiming reward: {}", id);

        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));

        if (reward.getIsClaimed()) {
            throw new IllegalStateException("Reward already claimed");
        }

        reward.setIsClaimed(true);
        reward.setClaimedAt(LocalDateTime.now());

        Reward claimedReward = rewardRepository.save(reward);
        return convertToDto(claimedReward);
    }

    @Override
    public Integer getTotalPoints(Long userId) {
        Integer totalPoints = rewardRepository.getTotalPointsByUserId(userId);
        return totalPoints != null ? totalPoints : 0;
    }

    @Override
    public void checkAndCreateMilestoneRewards(Long userId) {
        // Check savings milestones
        checkSavingsMilestones(userId);

        // Check transaction milestones
        checkTransactionMilestones(userId);
        logger.info("Checking milestone rewards for user: {}", userId);
    }

    private void checkSavingsMilestones(Long userId) {
        List<SavingsGoal> goals = savingsGoalRepository.findByUserIdAndStatus(userId, GoalStatus.ACTIVE);
        for (SavingsGoal goal : goals) {
            if (goal.getCurrentAmount().compareTo(goal.getTargetAmount()) >= 0) {
                if (!rewardRepository.existsByUserIdAndRewardType(userId, RewardType.SAVINGS_MILESTONE)) {
                    Reward reward = new Reward();
                    reward.setTitle("Savings Goal Achieved!");
                    reward.setDescription("You achieved your savings goal: " + goal.getName());
                    reward.setPoints(50);
                    reward.setRewardType(RewardType.SAVINGS_MILESTONE);
                    reward.setUser(goal.getUser());
                    reward.setIsClaimed(false);
                    rewardRepository.save(reward);
                }
            }
        }
    }

    private void checkTransactionMilestones(Long userId) {
        long transactionCount = transactionRepository.countByUserId(userId);
        if (transactionCount >= 100) {
            if (!rewardRepository.existsByUserIdAndRewardType(userId, RewardType.TRANSACTION_MILESTONE)) {
                Reward reward = new Reward();
                reward.setTitle("Transaction Milestone!");
                reward.setDescription("You completed 100 transactions!");
                reward.setPoints(100);
                reward.setRewardType(RewardType.TRANSACTION_MILESTONE);
                reward.setUser(transactionRepository.findUserById(userId));
                reward.setIsClaimed(false);
                rewardRepository.save(reward);
            }
        }
    }

    private RewardDto convertToDto(Reward reward) {
        return new RewardDto(
                reward.getId(),
                reward.getTitle(),
                reward.getDescription(),
                reward.getPoints(),
                reward.getRewardType(),
                reward.getUser().getId(),
                reward.getEarnedAt(),
                reward.getIsClaimed(),
                reward.getClaimedAt()
        );
    }

    private Reward convertToEntity(RewardDto dto) {
        Reward reward = new Reward();
        reward.setTitle(dto.getTitle());
        reward.setDescription(dto.getDescription());
        reward.setPoints(dto.getPoints());
        reward.setRewardType(dto.getRewardType());
        reward.setIsClaimed(false);
        return reward;
    }

    @Override
    @Transactional(readOnly = true)
    public RewardDto findById(Long id) {
        Reward reward = rewardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reward not found"));
        return convertToDto(reward);
    }
}

