/*
package no.stacc.payforjoy.service.impl;
import lombok.Builder;
import no.stacc.payforjoy.interfaces.repository.AccountRepository;
import no.stacc.payforjoy.interfaces.repository.UserRepository;
import no.stacc.payforjoy.model.dto.AccountDto;
import no.stacc.payforjoy.model.entity.Account;
import no.stacc.payforjoy.model.entity.User;
import no.stacc.payforjoy.enums.AccountType;
import no.stacc.payforjoy.enums.Currency;
import no.stacc.payforjoy.exception.custom.AccountNotFoundException;
import no.stacc.payforjoy.exception.custom.InsufficientFundsException;
import no.stacc.payforjoy.logging.PayForJoyLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AccountServiceImpl Tests")
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PayForJoyLogger logger;

    @InjectMocks
    private AccountServiceImpl accountService;

    private User sampleUser;
    private Account sampleAccount;
    private AccountDto sampleAccountDto;
    private Long userId;
    private Long accountId;

    @Builder
    @BeforeEach
    void setUp() {
        userId = 1L;
        accountId = 100L;

        sampleUser = User.builder()
                .id(userId)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        sampleAccount = Account.builder()
                .id(accountId)
                .accountNumber("ACC1234567890")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(5000.00))
                .currency(Currency.NOK)
                .user(sampleUser)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        sampleAccountDto = AccountDto.builder()
                .id(accountId)
                .accountNumber("ACC1234567890")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(5000.00))
                .currency(Currency.NOK)
                .owner("John Doe")
                .build();
    }

    @Nested
    @DisplayName("Find All Accounts By User ID")
    class FindAllByUserIdTests {

        @Test
        @DisplayName("Should return list of accounts for user")
        void shouldReturnListOfAccountsForUser() {
            // Given
            List<Account> accounts = Arrays.asList(sampleAccount);
            when(accountRepository.findByUserId(userId)).thenReturn(accounts);

            // When
            List<AccountDto> result = accountService.findAllByUserId(userId);

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(accountId);
            assertThat(result.get(0).getAccountNumber()).isEqualTo("ACC1234567890");
            verify(accountRepository, times(1)).findByUserId(userId);
        }

        @Test
        @DisplayName("Should return empty list when no accounts found")
        void shouldReturnEmptyListWhenNoAccountsFound() {
            // Given
            when(accountRepository.findByUserId(userId)).thenReturn(Arrays.asList());

            // When
            List<AccountDto> result = accountService.findAllByUserId(userId);

            // Then
            assertThat(result).isEmpty();
            verify(accountRepository, times(1)).findByUserId(userId);
        }
    }

    @Nested
    @DisplayName("Find Account By ID")
    class FindByIdTests {

        @Test
        @DisplayName("Should return account when found")
        void shouldReturnAccountWhenFound() {
            // Given
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(sampleAccount));

            // When
            AccountDto result = accountService.findById(accountId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(accountId);
            assertThat(result.getAccountNumber()).isEqualTo("ACC1234567890");
            verify(accountRepository, times(1)).findById(accountId);
        }

        @Test
        @DisplayName("Should throw exception when account not found")
        void shouldThrowExceptionWhenAccountNotFound() {
            // Given
            when(accountRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> accountService.findById(999L))
                    .isInstanceOf(AccountNotFoundException.class)
                    .hasMessageContaining("Account not found with ID: 999");

            verify(accountRepository, times(1)).findById(999L);
        }
    }

    @Nested
    @DisplayName("Create Account")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully")
        void shouldCreateAccountSuccessfully() {
            // Given
            AccountDto createDto = AccountDto.builder()
                    .accountType(AccountType.SAVINGS)
                    .balance(BigDecimal.valueOf(1000.00))
                    .currency(Currency.NOK)
                    .build();

            when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
            when(accountRepository.save(any(Account.class))).thenReturn(sampleAccount);

            // When
            AccountDto result = accountService.createAccount(createDto, userId);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(accountId);
            verify(userRepository, times(1)).findById(userId);
            verify(accountRepository, times(1)).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception when user not found")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            AccountDto createDto = AccountDto.builder().build();
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> accountService.createAccount(createDto, 999L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("User not found");

            verify(userRepository, times(1)).findById(999L);
            verify(accountRepository, never()).save(any(Account.class));
        }
    }

    @Nested
    @DisplayName("Transfer Funds")
    class TransferFundsTests {

        @Test
        @DisplayName("Should transfer funds successfully")
        void shouldTransferFundsSuccessfully() {
            // Given
            Account fromAccount = Account.builder()
                    .id(100L)
                    .balance(BigDecimal.valueOf(1000.00))
                    .build();

            Account toAccount = Account.builder()
                    .id(101L)
                    .balance(BigDecimal.valueOf(500.00))
                    .build();

            BigDecimal transferAmount = BigDecimal.valueOf(200.00);

            when(accountRepository.findById(100L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(101L)).thenReturn(Optional.of(toAccount));
            when(accountRepository.save(any(Account.class))).thenReturn(fromAccount, toAccount);

            // When
            accountService.transferFunds(100L, 101L, transferAmount);

            // Then
            assertThat(fromAccount.getBalance()).isEqualTo(BigDecimal.valueOf(800.00));
            assertThat(toAccount.getBalance()).isEqualTo(BigDecimal.valueOf(700.00));
            verify(accountRepository, times(2)).save(any(Account.class));
        }

        @Test
        @DisplayName("Should throw exception for insufficient funds")
        void shouldThrowExceptionForInsufficientFunds() {
            // Given
            Account fromAccount = Account.builder()
                    .id(100L)
                    .balance(BigDecimal.valueOf(100.00))
                    .build();

            Account toAccount = Account.builder()
                    .id(101L)
                    .balance(BigDecimal.valueOf(500.00))
                    .build();

            BigDecimal transferAmount = BigDecimal.valueOf(200.00);

            when(accountRepository.findById(100L)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(101L)).thenReturn(Optional.of(toAccount));

            // When & Then
            assertThatThrownBy(() -> accountService.transferFunds(100L, 101L, transferAmount))
                    .isInstanceOf(InsufficientFundsException.class)
                    .hasMessageContaining("Insufficient funds for transfer");

            verify(accountRepository, never()).save(any(Account.class));
        }
    }

    @Nested
    @DisplayName("Get Total Balance")
    class GetTotalBalanceTests {

        @Test
        @DisplayName("Should calculate total balance correctly")
        void shouldCalculateTotalBalanceCorrectly() {
            // Given
            Account account1 = Account.builder().balance(BigDecimal.valueOf(1000.00)).build();
            Account account2 = Account.builder().balance(BigDecimal.valueOf(2500.00)).build();
            List<Account> accounts = Arrays.asList(account1, account2);

            when(accountRepository.findByUserId(userId)).thenReturn(accounts);

            // When
            BigDecimal totalBalance = accountService.getTotalBalance(userId);

            // Then
            assertThat(totalBalance).isEqualTo(BigDecimal.valueOf(3500.00));
            verify(accountRepository, times(1)).findByUserId(userId);
        }

        @Test
        @DisplayName("Should return zero when no accounts")
        void shouldReturnZeroWhenNoAccounts() {
            // Given
            when(accountRepository.findByUserId(userId)).thenReturn(Arrays.asList());

            // When
            BigDecimal totalBalance = accountService.getTotalBalance(userId);

            // Then
            assertThat(totalBalance).isEqualTo(BigDecimal.ZERO);
            verify(accountRepository, times(1)).findByUserId(userId);
        }
    }
}

*/
