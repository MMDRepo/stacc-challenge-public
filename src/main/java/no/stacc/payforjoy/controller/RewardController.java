package no.stacc.payforjoy.controller;

import no.stacc.payforjoy.constants.ApiEndPoints;
import no.stacc.payforjoy.interfaces.service.RewardService;
import no.stacc.payforjoy.model.dto.RewardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping( "/api/v1/rewards")
public class RewardController {

    private final RewardService rewardService;

    @Autowired
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RewardDto>> getUserRewards(@PathVariable Long userId) {
        List<RewardDto> rewards = rewardService.findAllByUserId(userId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/user/{userId}/unclaimed")
    public ResponseEntity<List<RewardDto>> getUnclaimedRewards(@PathVariable Long userId) {
        List<RewardDto> rewards = rewardService.findUnclaimedByUserId(userId);
        return ResponseEntity.ok(rewards);
    }

    @GetMapping("/user/{userId}/points")
    public ResponseEntity<Integer> getTotalPoints(@PathVariable Long userId) {
        Integer totalPoints = rewardService.getTotalPoints(userId);
        return ResponseEntity.ok(totalPoints);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RewardDto> getReward(@PathVariable Long id) {
        RewardDto reward = rewardService.findById(id);
        return ResponseEntity.ok(reward);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<RewardDto> createReward(
            @PathVariable Long userId,
            @Valid @RequestBody RewardDto rewardDto) {
        RewardDto createdReward = rewardService.createReward(rewardDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReward);
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<RewardDto> claimReward(@PathVariable Long id) {
        RewardDto claimedReward = rewardService.claimReward(id);
        return ResponseEntity.ok(claimedReward);
    }

    @GetMapping("/user/{userId}/check-milestones")
    public ResponseEntity<Void> checkMilestoneRewards(@PathVariable Long userId) {
        rewardService.checkAndCreateMilestoneRewards(userId);
        return ResponseEntity.ok().build();
    }
}
