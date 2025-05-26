/*
package no.stacc.payforjoy.repository;

import no.stacc.payforjoy.interfaces.repository.AccountRepository;
import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.model.entity.User;
import no.stacc.payforjoy.enums.AccountType;
import no.stacc.payforjoy.enums.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("AccountRepository Integration Tests")
class AccountRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AccountRepository accountRepository;

    private User testUser;
    private Account testAccount;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+47 123 45 678")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        entityManager.persistAndFlush(testUser);

        testAccount = Account.builder()
                .accountNumber("ACC1234567890")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(5000.00))
                .currency(Currency.NOK)
                .user(testUser)
                .build();

        entityManager.persistAndFlush(testAccount);
    }

    @Test
    @DisplayName("Should find accounts by user ID")
    void shouldFindAccountsByUserId() {
        // When
        List<Account> accounts = accountRepository.findByUserId(testUser.getId());

        // Then
        assertThat(accounts).hasSize(1);
        assertThat(accounts.get(0).getAccountNumber()).isEqualTo("ACC1234567890");
        assertThat(accounts.get(0).getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Should find account by account number")
    void shouldFindAccountByAccountNumber() {
        // When
        Optional<Account> account = accountRepository.findByAccountNumber("ACC1234567890");

        // Then
        assertThat(account).isPresent();
        assertThat(account.get().getAccountNumber()).isEqualTo("ACC1234567890");
        assertThat(account.get().getBalance()).isEqualTo(BigDecimal.valueOf(5000.00));
    }

    @Test
    @DisplayName("Should check if account number exists")
    void shouldCheckIfAccountNumberExists() {
        // When
        boolean exists = accountRepository.existsByAccountNumber("ACC1234567890");
        boolean notExists = accountRepository.existsByAccountNumber("NONEXISTENT");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should save new account")
    void shouldSaveNewAccount() {
        // Given
        Account newAccount = Account.builder()
                .accountNumber("ACC9876543210")
                .accountType(AccountType.SAVINGS)
                .balance(BigDecimal.valueOf(2500.00))
                .currency(Currency.NOK)
                .user(testUser)
                .build();

        // When
        Account savedAccount = accountRepository.save(newAccount);
        entityManager.flush();

        // Then
        assertThat(savedAccount.getId()).isNotNull();
        assertThat(savedAccount.getAccountNumber()).isEqualTo("ACC9876543210");
        assertThat(savedAccount.getCreatedAt()).isNotNull();
        assertThat(savedAccount.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should delete account")
    void shouldDeleteAccount() {
        // When
        accountRepository.delete(testAccount);
        entityManager.flush();

        // Then
        Optional<Account> deletedAccount = accountRepository.findById(testAccount.getId());
        assertThat(deletedAccount).isEmpty();
    }

    @Test
    @DisplayName("Should count accounts by user ID")
    void shouldCountAccountsByUserId() {
        // When
        long count = accountRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(1);
    }
}

*/
