package no.stacc.payforjoy.interfaces.service;

import jakarta.validation.Valid;
import no.stacc.payforjoy.model.dto.TransactionDto;

import no.stacc.payforjoy.model.dto.TransactionDto;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    List<TransactionDto> findAllByAccountId(Long accountId);
    List<TransactionDto> findAllByUserId(Long userId);
    List<TransactionDto> findRecentByUserId(Long userId, int limit);
    TransactionDto findById(Long id);
    TransactionDto createTransaction(TransactionDto transactionDto);
    TransactionDto updateTransaction(Long id, TransactionDto transactionDto);
    void deleteTransaction(Long id);
    BigDecimal calculateMonthlyIncome(Long userId);
    BigDecimal calculateMonthlyExpenses(Long userId);
}
