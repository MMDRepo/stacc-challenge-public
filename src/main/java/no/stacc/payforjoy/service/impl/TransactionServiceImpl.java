package no.stacc.payforjoy.service.impl;

import no.stacc.payforjoy.interfaces.service.TransactionService;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.model.entity.Transaction;
import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.interfaces.repository.TransactionRepository;
import no.stacc.payforjoy.interfaces.repository.AccountRepository;
import no.stacc.payforjoy.exception.custom.AccountNotFoundException;
import no.stacc.payforjoy.exception.custom.InvalidTransactionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Override
    public List<TransactionDto> findAllByAccountId(Long accountId) {
        return List.of();
    }

    @Override
    public List<TransactionDto> findAllByUserId(Long userId) {
        return List.of();
    }

    @Override
    public List<TransactionDto> findRecentByUserId(Long userId, int limit) {
        return List.of();
    }

    @Override
    public TransactionDto findById(Long id) {
        return null;
    }

    @Override
    public TransactionDto createTransaction(TransactionDto transactionDto) {
        log.info("Creating new transaction: {} for amount: {}",
                transactionDto.getDescription(), transactionDto.getAmount());

        Account account = findAccountEntity(transactionDto.getAccountId());
        validateTransaction(transactionDto, account);

        // Using Lombok @Builder pattern
        Transaction transaction = Transaction.builder()
                .transactionDate(transactionDto.getTransactionDate() != null ?
                        transactionDto.getTransactionDate() : LocalDateTime.now())
                .description(transactionDto.getDescription())
                .amount(transactionDto.getAmount())
                .transactionType(transactionDto.getTransactionType())
                .currency(transactionDto.getCurrency())
                .category(transactionDto.getCategory())
                .account(account)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        updateAccountBalance(account, transactionDto);

        log.info("Transaction created successfully with ID: {}", saved.getId());
        return convertToDto(saved);
    }

    @Override
    public TransactionDto updateTransaction(Long id, TransactionDto transactionDto) {
        return null;
    }

    @Override
    public void deleteTransaction(Long id) {

    }

    @Override
    public BigDecimal calculateMonthlyIncome(Long userId) {
        return null;
    }

    @Override
    public BigDecimal calculateMonthlyExpenses(Long userId) {
        return null;
    }

    /*@Override
    @Transactional(readOnly = true)
    public List<TransactionDto> findByAccountId(Long accountId) {
        log.debug("Fetching transactions for account: {}", accountId);
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId);
        return transactions.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto> findByUserId(Long userId, Pageable pageable) {
        log.debug("Fetching transactions for user: {} with pagination", userId);
        Page<Transaction> transactions = transactionRepository.findByAccountUserIdOrderByTransactionDateDesc(userId, pageable);
        return transactions.map(this::convertToDto);
    }*/

    // Helper methods
    private Account findAccountEntity(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.error("Account not found with ID: {}", accountId);
                    return new AccountNotFoundException("Account not found with ID: " + accountId);
                });
    }

    private void validateTransaction(TransactionDto dto, Account account) {
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            log.error("Invalid transaction amount: {}", dto.getAmount());
            throw new InvalidTransactionException("Transaction amount must be greater than 0");
        }

        // Check for sufficient funds if it's an expense
        if (account.getBalance().compareTo(dto.getAmount()) < 0) {
            log.error("Insufficient funds for transaction. Account balance: {}, Transaction amount: {}",
                    account.getBalance(), dto.getAmount());
            throw new InvalidTransactionException("Insufficient funds for transaction");
        }
    }

    private void updateAccountBalance(Account account, TransactionDto dto) {
       /* if (dto.isExpense()) {
            account.subtractFunds(dto.getAmount());
        } else if (dto.isIncome()) {
            account.addFunds(dto.getAmount());
        }*/
        accountRepository.save(account);
        log.debug("Account balance updated for account: {}", account.getAccountNumber());
    }

    private TransactionDto convertToDto(Transaction transaction) {
        return TransactionDto.builder()
                .id(transaction.getId())
                .transactionDate(transaction.getTransactionDate())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .currency(transaction.getCurrency())
                .category(transaction.getCategory())
                .accountId(transaction.getAccount().getId())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}

