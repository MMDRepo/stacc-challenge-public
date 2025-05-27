package no.stacc.payforjoy.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionDto {
    private Long id;
    private LocalDateTime transactionDate;
    private String description;
    private BigDecimal amount;
    private String transactionType;
    private String currency;
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String category;
    private LocalDateTime createdAt;

    // Add a public constructor
    public TransactionDto(Long id, LocalDateTime transactionDate, String description, BigDecimal amount,
                          String transactionType, String currency, Long accountId, Long userId,
                          String accountNumber, String category, LocalDateTime createdAt) {
        this.id = id;
        this.transactionDate = transactionDate;
        this.description = description;
        this.amount = amount;
        this.transactionType = transactionType;
        this.currency = currency;
        this.accountId = accountId;
        this.userId = userId;
        this.accountNumber = accountNumber;
        this.category = category;
        this.createdAt = createdAt;
    }
}