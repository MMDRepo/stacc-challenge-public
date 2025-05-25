package no.stacc.payforjoy.service.impl;

import no.stacc.payforjoy.interfaces.service.RewardService;
import no.stacc.payforjoy.interfaces.repository.RewardRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.model.dto.RewardDto;
import no.stacc.payforjoy.model.entity.Reward;
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

    @Autowired
    public RewardServiceImpl(RewardRepository rewardRepository,
                             UserRepository userRepository,
                             PayForJoyLogger logger) {
        this.rewardRepository = rewardRepository;
        this.userRepository = userRepository;
        this.logger = logger;
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
        // This would contain logic to check user's savings milestones
        // and create appropriate rewards
        logger.info("Checking milestone rewards for user: {}", userId);
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

