package no.stacc.payforjoy.interfaces.service;

import jakarta.validation.Valid;
import no.stacc.payforjoy.model.dto.AccountDto;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {
    List<AccountDto> findAllByUserId(Long userId);

    AccountDto findById(Long id);

    AccountDto createAccount(@Valid AccountDto accountDto, Long userId);

    AccountDto updateAccount(Long id, @Valid AccountDto accountDto);

    void deleteAccount(Long id);

    BigDecimal getTotalBalance(Long userId);

    void transferFunds(Long fromId, Long toId, BigDecimal amount);
}
