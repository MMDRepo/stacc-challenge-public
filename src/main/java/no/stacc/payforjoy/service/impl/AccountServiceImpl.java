package no.stacc.payforjoy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.stacc.payforjoy.exception.custom.AccountNotFoundException;
import no.stacc.payforjoy.exception.custom.InsufficientFundsException;
import no.stacc.payforjoy.interfaces.repository.AccountRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.interfaces.service.AccountService;
import no.stacc.payforjoy.model.dto.AccountDto;
import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.model.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor // Lombok generates constructor for final fields
@Slf4j // Lombok generates logger field
public class AccountServiceImpl implements AccountService {

    // Final fields for constructor injection - Lombok @RequiredArgsConstructor
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AccountDto> findAllByUserId(Long userId) {
        log.info("Fetching accounts for user: {}", userId);
        List<Account> accounts = accountRepository.findByUserId(userId);
        List<AccountDto> accountDtos = accounts.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

        log.debug("Found {} accounts for user: {}", accountDtos.size(), userId);
        return accountDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public AccountDto findById(Long id) {
        log.info("Fetching account with ID: {}", id);
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new AccountNotFoundException("Account not found with ID: " + id);
                });
        return convertToDto(account);
    }

    @Override
    public AccountDto createAccount(AccountDto accountDto, Long userId) {
        log.info("Creating new account for user: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });

        // Using Lombok @Builder pattern
        Account account = Account.builder()
                .accountType(accountDto.getAccountType())
                .balance(accountDto.getBalance() != null ? accountDto.getBalance() : BigDecimal.ZERO)
                .currency(accountDto.getCurrency())
                .user(user)
                .accountNumber(generateAccountNumber())
                .build();

        Account savedAccount = accountRepository.save(account);
        log.info("Account created successfully with ID: {} for user: {}",
                savedAccount.getId(), userId);

        return convertToDto(savedAccount);
    }

    @Override
    public void transferFunds(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        log.info("Transferring {} from account {} to account {}",
                amount, fromAccountId, toAccountId);

        Account fromAccount = findAccountEntity(fromAccountId);
        Account toAccount = findAccountEntity(toAccountId);

        validateTransfer(fromAccount, amount);

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        log.info("Transfer completed successfully: {} transferred from {} to {}",
                amount, fromAccountId, toAccountId);
    }

    @Override
    public BigDecimal getTotalBalance(Long userId) {
        log.debug("Calculating total balance for user: {}", userId);
        BigDecimal totalBalance = accountRepository.findByUserId(userId).stream()
                .map(Account::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.debug("Total balance for user {}: {}", userId, totalBalance);
        return totalBalance;
    }

    @Override
    public AccountDto updateAccount(Long id, AccountDto accountDto) {
        log.info("Updating account with ID: {}", id);
        Account account = findAccountEntity(id);

        // Using Lombok @With for immutable updates (if DTO was @Value)
        // Or direct field updates for mutable entities
        account.setAccountType(accountDto.getAccountType());
        account.setCurrency(accountDto.getCurrency());

        Account updated = accountRepository.save(account);
        log.info("Account updated successfully: {}", id);
        return convertToDto(updated);
    }

    @Override
    public void deleteAccount(Long id) {
        log.info("Deleting account with ID: {}", id);
        Account account = findAccountEntity(id);

        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            log.error("Cannot delete account {} with positive balance: {}",
                    id, account.getBalance());
            throw new IllegalStateException("Cannot delete account with positive balance");
        }

        accountRepository.delete(account);
        log.info("Account deleted successfully: {}", id);
    }

    // Helper methods
    private Account findAccountEntity(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", id);
                    return new AccountNotFoundException("Account not found with ID: " + id);
                });
    }

    private void validateTransfer(Account fromAccount, BigDecimal amount) {
        if (fromAccount.getBalance().compareTo(amount) < 0) {
            log.error("Insufficient funds for transfer. Account: {}, Balance: {}, Amount: {}",
                    fromAccount.getAccountNumber(), fromAccount.getBalance(), amount);
            throw new InsufficientFundsException("Insufficient funds for transfer");
        }
    }

    private AccountDto convertToDto(Account account) {
        // Using Lombok @Builder for DTO creation
        return AccountDto.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .owner(account.getUser().getFirstName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

    private String generateAccountNumber() {
        String accountNumber = "ACC" + System.currentTimeMillis();
        log.debug("Generated account number: {}", accountNumber);
        return accountNumber;
    }
}

