/*
package no.stacc.payforjoy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.stacc.payforjoy.interfaces.service.TransactionService;
import no.stacc.payforjoy.model.dto.TransactionDto;
import no.stacc.payforjoy.enums.TransactionType;
import no.stacc.payforjoy.enums.Currency;
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
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@DisplayName("TransactionController Tests")
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDto sampleTransactionDto;
    private Long userId;
    private Long transactionId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        transactionId = 200L;

        sampleTransactionDto = TransactionDto.builder()
                .id(transactionId)
                .transactionDate(LocalDateTime.now())
                .description("Grocery Shopping")
                .amount(BigDecimal.valueOf(450.00))
                .transactionType(TransactionType.EXPENSE)
                .currency(Currency.NOK)
                .accountId(100L)
                .category("Food & Dining")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/v1/transactions/user/{userId}")
    class GetUserTransactionsTests {

        @Test
        @DisplayName("Should return user transactions successfully")
        void shouldReturnUserTransactionsSuccessfully() throws Exception {
            // Given
            List<TransactionDto> transactions = Arrays.asList(sampleTransactionDto);
            when(transactionService.findAllByUserId(userId)).thenReturn(transactions);

            // When & Then
            mockMvc.perform(get("/api/v1/transactions/user/{userId}", userId))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(transactionId.intValue())))
                    .andExpect(jsonPath("$[0].description", is("Grocery Shopping")))
                    .andExpect(jsonPath("$[0].amount", is(450.00)))
                    .andExpect(jsonPath("$[0].transactionType", is("EXPENSE")));

            verify(transactionService, times(1)).findAllByUserId(userId);
        }
    }

    @Nested
    @DisplayName("POST /api/v1/transactions")
    class CreateTransactionTests {

        @Test
        @DisplayName("Should create transaction successfully")
        void shouldCreateTransactionSuccessfully() throws Exception {
            // Given
            TransactionDto createTransactionDto = TransactionDto.builder()
                    .transactionDate(LocalDateTime.now())
                    .description("Coffee Purchase")
                    .amount(BigDecimal.valueOf(65.00))
                    .transactionType(TransactionType.EXPENSE)
                    .currency(Currency.NOK)
                    .accountId(100L)
                    .category("Food & Dining")
                    .build();

            when(transactionService.createTransaction(any(TransactionDto.class)))
                    .thenReturn(sampleTransactionDto);

            // When & Then
            mockMvc.perform(post("/api/v1/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createTransactionDto)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(transactionId.intValue())));

            verify(transactionService, times(1)).createTransaction(any(TransactionDto.class));
        }

        @Test
        @DisplayName("Should return bad request for invalid transaction data")
        void shouldReturnBadRequestForInvalidTransactionData() throws Exception {
            // Given - Invalid transaction with negative amount
            TransactionDto invalidTransactionDto = TransactionDto.builder()
                    .amount(BigDecimal.valueOf(-100.00))
                    .build();

            // When & Then
            mockMvc.perform(post("/api/v1/transactions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidTransactionDto)))
                    .andExpect(status().isBadRequest());

            verify(transactionService, never()).createTransaction(any(TransactionDto.class));
        }
    }
}
*/
