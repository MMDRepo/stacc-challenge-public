package no.stacc.payforjoy.service.util;

import no.stacc.payforjoy.logging.PayForJoyLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class NotificationService {

    private final PayForJoyLogger logger;

    @Autowired
    public NotificationService(PayForJoyLogger logger) {
        this.logger = logger;
    }

    public void sendGoalCompletionNotification(Long userId, String goalName) {
        logger.info("Sending goal completion notification to user {}: Goal '{}' completed!", userId, goalName);
        // In a real implementation, this would send email, push notification, etc.
    }

    public void sendMilestoneNotification(Long userId, String milestone) {
        logger.info("Sending milestone notification to user {}: {}", userId, milestone);
    }

    public void sendRewardNotification(Long userId, String rewardTitle, Integer points) {
        logger.info("Sending reward notification to user {}: Earned '{}' ({} points)", userId, rewardTitle, points);
    }

    public void sendLowBalanceAlert(Long userId, String accountNumber, BigDecimal balance) {
        logger.warn("Low balance alert for user {}: Account {} has balance {}", userId, accountNumber, balance);
    }
}


