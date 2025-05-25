package no.stacc.payforjoy.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import no.stacc.payforjoy.enums.RewardType;

import java.time.LocalDateTime;

@Entity
@Table(name = "rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Enumerated(EnumType.STRING)
    @Column(name = "reward_type", nullable = false)
    private RewardType rewardType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "earned_at")
    private LocalDateTime earnedAt;

    @Column(name = "is_claimed")
    private Boolean isClaimed = false;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    @PrePersist
    protected void onCreate() {
        if (earnedAt == null) {
            earnedAt = LocalDateTime.now();
        }
    }
}
