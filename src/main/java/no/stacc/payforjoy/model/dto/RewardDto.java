package no.stacc.payforjoy.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import no.stacc.payforjoy.enums.RewardType;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RewardDto {
    private Long id;
    private String title;
    private String description;
    private Integer points;
    private RewardType rewardType;
    private Long userId;
    private LocalDateTime earnedAt;
    private Boolean isClaimed;
    private LocalDateTime claimedAt;
}


