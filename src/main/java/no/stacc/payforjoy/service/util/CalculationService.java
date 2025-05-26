package no.stacc.payforjoy.service.util;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class CalculationService {

    public BigDecimal calculatePercentage(BigDecimal current, BigDecimal target) {
        if (target.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return current.divide(target, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal calculateDailyTarget(BigDecimal remainingAmount, LocalDate targetDate) {
        long daysRemaining = ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        if (daysRemaining <= 0) {
            return remainingAmount;
        }
        return remainingAmount.divide(BigDecimal.valueOf(daysRemaining), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal calculateCompoundInterest(BigDecimal principal, BigDecimal rate, int years) {
        BigDecimal compound = BigDecimal.ONE.add(rate);
        for (int i = 0; i < years; i++) {
            principal = principal.multiply(compound);
        }
        return principal;
    }

    public int calculateRewardPoints(BigDecimal savingsAmount) {
        // 1 point per NOK saved
        return savingsAmount.intValue();
    }
}

