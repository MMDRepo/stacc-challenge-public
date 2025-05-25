package no.stacc.payforjoy.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import no.stacc.payforjoy.enums.Currency;
import no.stacc.payforjoy.enums.GoalStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavingsGoalDto {
    private Long id;
    private String name;
    private String description;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private GoalStatus status;
    private Currency currency;
    private BigDecimal progressPercentage;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
