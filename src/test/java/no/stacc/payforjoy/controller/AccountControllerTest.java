/*
package no.stacc.payforjoy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.stacc.payforjoy.interfaces.service.AccountService;
import no.stacc.payforjoy.model.dto.AccountDto;
import no.stacc.payforjoy.enums.AccountType;
import no.stacc.payforjoy.enums.Currency;
import no.stacc.payforjoy.exception.custom.AccountNotFoundException;
import no.stacc.payforjoy.exception.custom.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@DisplayName("AccountController Tests")
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountDto sampleAccountDto;
    private Long userId;
    private Long accountId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        accountId = 100L;

        sampleAccountDto = AccountDto.builder()
                .id(accountId)
                .accountNumber("ACC1234567890")
                .accountType(AccountType.CHECKING)
                .balance(BigDecimal.valueOf(5000.00))
                .currency(Currency.NOK)
                .owner("John Doe")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/accounts/user/{userId}")
    class GetUserAccountsTests {

        @Test
        @DisplayName("Should return user accounts successfully")
        void shouldReturnUserAccountsSuccessfully() throws Exception {
            // Given
            List<AccountDto> accounts = Arrays.asList(sampleAccountDto);
            when(accountService.findAllByUserId(userId)).thenReturn(accounts);

            // When & Then
            mockMvc.perform(get("/api/v1/accounts/user/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(accountId.intValue())))
                    .andExpect(jsonPath("$[0].accountNumber", is("ACC1234567890")))
                    .andExpect(jsonPath("$[0].accountType", is("CHECKING")))
                    .andExpect(jsonPath("$[0].balance", is(5000.00)));

            verify(accountService, times(1)).findAllByUserId(userId);
        }

        @Test
        @DisplayName("Should return empty list when user has no accounts")
        void shouldReturnEmptyListWhenUserHasNoAccounts() throws Exception {
            // Given
            when(accountService.findAllByUserId(userId)).thenReturn(Collections.emptyList());

            // When & Then
            mockMvc.perform(get("/api/v1/accounts/user/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));

            verify(accountService, times(1)).findAllByUserId(userId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/accounts/{id}")
    class GetAccountTests {

        @Test
        @DisplayName("Should return account by ID successfully")
        void shouldReturnAccountByIdSuccessfully() throws Exception {
            // Given
            when(accountService.findById(accountId)).thenReturn(sampleAccountDto);

            // When & Then
            mockMvc.perform(get("/api/v1/accounts/{id}", accountId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(accountId.intValue())))
                    .andExpect(jsonPath("$.accountNumber", is("ACC1234567890")))
                    .andExpect(jsonPath("$.balance", is(5000.00)));

            verify(accountService, times(1)).findById(accountId);
        }

        @Test
        @DisplayName("Should return 404 when account not found")
        void shouldReturn404WhenAccountNotFound() throws Exception {
            // Given
            when(accountService.findById(999L)).thenThrow(new AccountNotFoundException("Account not found"));

            // When & Then
            mockMvc.perform(get("/api/v1/accounts/{id}", 999L))
                    .andExpect(status().isNotFound());

            verify(accountService, times(1)).findById(999L);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/accounts/user/{userId}")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully")
        void shouldCreateAccountSuccessfully() throws Exception {
            // Given
            AccountDto createAccountDto = AccountDto.builder()
                    .accountType(AccountType.SAVINGS)
                    .balance(BigDecimal.valueOf(1000.00))
                    .currency(Currency.NOK)
                    .build();

            when(accountService.createAccount(any(AccountDto.class), eq(userId)))
                    .thenReturn(sampleAccountDto);

            // When & Then
            mockMvc.perform(post("/api/v1/accounts/user/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createAccountDto)))
                    .andExpect(status().isCreated())
                    //.andExpected(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(accountId.intValue())));

            verify(accountService, times(1)).createAccount(any(AccountDto.class), eq(userId));
        }

        @Test
        @DisplayName("Should return bad request for invalid account data")
        void shouldReturnBadRequestForInvalidAccountData() throws Exception {
            // Given - Invalid account with null required fields
            AccountDto invalidAccountDto = AccountDto.builder().build();

            // When & Then
            mockMvc.perform(post("/api/v1/accounts/user/{userId}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidAccountDto)))
                    .andExpect(status().isBadRequest());

            verify(accountService, never()).createAccount(any(AccountDto.class), any(Long.class));
        }
    }

    @Nested
    @DisplayName("POST /api/v1/accounts/{fromId}/transfer/{toId}")
    class TransferFundsTests {

        @Test
        @DisplayName("Should transfer funds successfully")
        void shouldTransferFundsSuccessfully() throws Exception {
            // Given
            Long fromAccountId = 100L;
            Long toAccountId = 101L;
            BigDecimal transferAmount = BigDecimal.valueOf(250.00);

            doNothing().when(accountService).transferFunds(fromAccountId, toAccountId, transferAmount);

            // When & Then
            mockMvc.perform(post("/api/v1/accounts/{fromId}/transfer/{toId}", fromAccountId, toAccountId)
                            .param("amount", transferAmount.toString()))
                    .andExpect(status().isOk());

            verify(accountService, times(1)).transferFunds(fromAccountId, toAccountId, transferAmount);
        }

        @Test
        @DisplayName("Should return bad request for insufficient funds")
        void shouldReturnBadRequestForInsufficientFunds() throws Exception {
            // Given
            Long fromAccountId = 100L;
            Long toAccountId = 101L;
            BigDecimal transferAmount = BigDecimal.valueOf(10000.00);

            doThrow(new InsufficientFundsException("Insufficient funds"))
                    .when(accountService).transferFunds(fromAccountId, toAccountId, transferAmount);

            // When & Then
            mockMvc.perform(post("/api/v1/accounts/{fromId}/transfer/{toId}", fromAccountId, toAccountId)
                            .param("amount", transferAmount.toString()))
                    .andExpect(status().isBadRequest());

            verify(accountService, times(1)).transferFunds(fromAccountId, toAccountId, transferAmount);
        }
    }
}
*/
