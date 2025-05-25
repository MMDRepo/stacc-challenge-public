package no.stacc.payforjoy.model.dto;

import no.stacc.payforjoy.enums.Currency;
import no.stacc.payforjoy.enums.TransactionType;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDto {
    private Long id;
    private LocalDateTime transactionDate;
    private String description;
    private BigDecimal amount;
    private TransactionType transactionType;
    private Currency currency;
    private Long accountId;
    private String accountNumber;
    private String category;
    private LocalDateTime createdAt;

}

