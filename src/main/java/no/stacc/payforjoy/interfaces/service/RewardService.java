package no.stacc.payforjoy.interfaces.service;

import no.stacc.payforjoy.model.dto.RewardDto;

import java.util.List;

public interface RewardService {
    List<RewardDto> findAllByUserId(Long userId);
    List<RewardDto> findUnclaimedByUserId(Long userId);
    RewardDto findById(Long id);
    RewardDto createReward(RewardDto rewardDto, Long userId);
    RewardDto claimReward(Long id);
    void checkAndCreateMilestoneRewards(Long userId);
    Integer getTotalPoints(Long userId);
}
