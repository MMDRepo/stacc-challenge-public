package no.stacc.payforjoy.controller;

import no.stacc.payforjoy.enums.Currency;
import no.stacc.payforjoy.enums.TransactionType;
import no.stacc.payforjoy.interfaces.repository.AccountRepository;
import no.stacc.payforjoy.interfaces.repository.TransactionRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.interfaces.service.TransactionService;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.model.entity.Transaction;
import no.stacc.payforjoy.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public TransactionController(TransactionService transactionService,
                                 UserRepository userRepository,
                                 AccountRepository accountRepository,
                                 TransactionRepository transactionRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(@PathVariable Long userId) {
        List<TransactionDto> transactions = transactionService.findAllByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<TransactionDto>> getRecentTransactions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionDto> transactions = transactionService.findRecentByUserId(userId, limit);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransaction(@PathVariable Long id) {
        TransactionDto transaction = transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(@Valid @RequestBody TransactionDto transactionDto) {
        System.out.println(transactionDto);


        TransactionDto createdTransaction = transactionService.createTransaction(transactionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionDto transactionDto) {
        TransactionDto updatedTransaction = transactionService.updateTransaction(id, transactionDto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}