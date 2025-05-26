package no.stacc.payforjoy.model.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import no.stacc.payforjoy.enums.AccountType;
import no.stacc.payforjoy.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private Currency currency;
    private String owner;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

