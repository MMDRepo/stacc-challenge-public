package no.stacc.payforjoy.controller;

import no.stacc.payforjoy.interfaces.service.AccountService;
import no.stacc.payforjoy.model.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping( "/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getUserAccounts(@PathVariable Long userId) {
        List<AccountDto> accounts = accountService.findAllByUserId(userId);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccount(@PathVariable Long id) {
        AccountDto account = accountService.findById(id);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/user/{userId}")
    public ResponseEntity<AccountDto> createAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountDto accountDto) {
        AccountDto createdAccount = accountService.createAccount(accountDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> updateAccount(
            @PathVariable Long id,
            @Valid @RequestBody AccountDto accountDto) {
        AccountDto updatedAccount = accountService.updateAccount(id, accountDto);
        return ResponseEntity.ok(updatedAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/total-balance")
    public ResponseEntity<BigDecimal> getTotalBalance(@PathVariable Long userId) {
        BigDecimal totalBalance = accountService.getTotalBalance(userId);
        return ResponseEntity.ok(totalBalance);
    }

    @PostMapping("/{fromId}/transfer/{toId}")
    public ResponseEntity<Void> transferFunds(
            @PathVariable Long fromId,
            @PathVariable Long toId,
            @RequestParam BigDecimal amount) {
        accountService.transferFunds(fromId, toId, amount);
        return ResponseEntity.ok().build();
    }
}
