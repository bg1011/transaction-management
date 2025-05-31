package com.hsbc.transaction.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsbc.transaction.common.enums.TransactionType;
import com.hsbc.transaction.common.exception.BusinessException;
import com.hsbc.transaction.common.exception.ErrorCode;
import com.hsbc.transaction.common.response.ApiResponse;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String API_BASE_URL = "/api/transactions";
    private static final String IDEMPOTENCY_KEY = "test-key-123";

    private TransactionVO mockTransactionVO;
    private Transaction mockTransaction;
    private CreateTransactionDTO createTransactionDTO;
    private UpdateTransactionDTO updateTransactionDTO;

    @BeforeEach
    void setUp() {
        // Reset mocks before each test
        reset(transactionService);

        LocalDateTime now = LocalDateTime.now();
        mockTransactionVO = TransactionVO.builder()
                .id(1L)
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .createdAt(now)
                .updatedAt(now)
                .build();

        mockTransaction = Transaction.builder()
                .id(1L)
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .createdAt(now)
                .updatedAt(now)
                .build();

        createTransactionDTO = CreateTransactionDTO.builder()
                .description("Test Transaction")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .build();

        updateTransactionDTO = UpdateTransactionDTO.builder()
                .description("Updated Transaction")
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.EXPENSE)
                .build();
    }

    @Nested
    @DisplayName("GET /api/transactions")
    class GetAllTransactionsTests {

        @Test
        @DisplayName("Should return paginated transactions successfully")
        void whenGetAllTransactions_thenReturn200() throws Exception {
            // Arrange
            List<TransactionVO> transactions = List.of(
                    createTestTransactionVo(1L, "Salary", TransactionType.INCOME),
                    createTestTransactionVo(2L, "Rent", TransactionType.EXPENSE)
            );
            Page<TransactionVO> page = new PageImpl<>(transactions);

            when(transactionService.getAllTransactions(any(Pageable.class))).thenReturn(page);

            // Act & Assert
            mockMvc.perform(get(API_BASE_URL)
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "id,desc")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content[0].description").value("Salary"))
                    .andExpect(jsonPath("$.data.totalElements").value(2));
        }

        @Test
        @DisplayName("Should return 400 for invalid sort parameter")
        void whenGetAllTransactionsWithInvalidSort_thenReturn400() throws Exception {
            mockMvc.perform(get(API_BASE_URL)
                    .param("sort", "invalidField")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return empty page when no transactions exist")
        void whenNoTransactions_thenReturnEmptyPage() throws Exception {
            // Arrange
            Page<TransactionVO> emptyPage = Page.empty();
            when(transactionService.getAllTransactions(any(Pageable.class))).thenReturn(emptyPage);

            // Act & Assert
            mockMvc.perform(get(API_BASE_URL)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/transactions/{id}")
    class GetTransactionByIdTests {

        @Test
        @DisplayName("Should return transaction when found")
        void whenGetTransactionById_thenReturn200() throws Exception {
            // Arrange
            when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(mockTransaction));

            // Act & Assert
            mockMvc.perform(get(API_BASE_URL + "/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(jsonPath("$.description").value("Test Transaction"))
                    .andExpect(jsonPath("$.type").value("INCOME"));
        }

        @Test
        @DisplayName("Should return 404 when transaction not found")
        void whenGetNonExistingTransaction_thenReturn404() throws Exception {
            // Arrange
            when(transactionService.getTransactionById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            mockMvc.perform(get(API_BASE_URL + "/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return 400 for invalid ID")
        void whenGetTransactionWithInvalidId_thenReturn400() throws Exception {
            mockMvc.perform(get(API_BASE_URL + "/{id}", 0L)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/transactions")
    class CreateTransactionTests {

        @Test
        @DisplayName("Should create transaction successfully")
        void whenCreateTransaction_thenReturn201() throws Exception {
            // Arrange
            when(transactionService.createTransaction(any(CreateTransactionDTO.class), anyString()))
                    .thenReturn(mockTransactionVO);

            // Act & Assert
            mockMvc.perform(post(API_BASE_URL)
                    .header("Idempotency-Key", IDEMPOTENCY_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createTransactionDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.description").value("Test Transaction"));
        }

        @Test
        @DisplayName("Should return 400 for invalid transaction data")
        void whenCreateInvalidTransaction_thenReturn400() throws Exception {
            // Arrange
            CreateTransactionDTO invalidTransaction = CreateTransactionDTO.builder()
                    .amount(BigDecimal.ZERO)
                    .build();

            // Act & Assert
            mockMvc.perform(post(API_BASE_URL)
                    .header("Idempotency-Key", IDEMPOTENCY_KEY)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(invalidTransaction)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when Idempotency-Key is missing")
        void whenCreateTransactionWithoutIdempotencyKey_thenReturn400() throws Exception {
            // Arrange
            CreateTransactionDTO newTransaction = CreateTransactionDTO.builder()
                    .description("New Transaction")
                    .amount(new BigDecimal("100.00"))
                    .type(TransactionType.INCOME)
                    .build();

            // Act & Assert
            mockMvc.perform(post(API_BASE_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newTransaction)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT /api/transactions/{id}")
    class UpdateTransactionTests {

        @Test
        @DisplayName("Should update transaction successfully")
        void whenUpdateTransaction_thenReturn200() throws Exception {
            // Arrange
            TransactionVO updatedTransactionVO = TransactionVO.builder()
                    .id(1L)
                    .description("Updated Transaction")
                    .amount(new BigDecimal("200.00"))
                    .type(TransactionType.EXPENSE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(transactionService.updateTransaction(eq(1L), any(UpdateTransactionDTO.class)))
                    .thenReturn(updatedTransactionVO);

            // Act & Assert
            mockMvc.perform(put(API_BASE_URL + "/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTransactionDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(1))
                    .andExpect(jsonPath("$.data.description").value("Updated Transaction"));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existing transaction")
        void whenUpdateNonExistingTransaction_thenReturn404() throws Exception {
            // Arrange
            when(transactionService.updateTransaction(eq(1L), any(UpdateTransactionDTO.class)))
                    .thenThrow(new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

            // Act & Assert
            mockMvc.perform(put(API_BASE_URL + "/{id}", 1L)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateTransactionDTO)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("DELETE /api/transactions/{id}")
    class DeleteTransactionTests {

        @Test
        @DisplayName("Should delete transaction successfully")
        void whenDeleteTransaction_thenReturn204() throws Exception {
            // Arrange
            doNothing().when(transactionService).deleteTransaction(1L);

            // Act & Assert
            mockMvc.perform(delete(API_BASE_URL + "/{id}", 1L))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existing transaction")
        void whenDeleteNonExistingTransaction_thenReturn404() throws Exception {
            // Arrange
            doThrow(new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND))
                    .when(transactionService).deleteTransaction(1L);

            // Act & Assert
            mockMvc.perform(delete(API_BASE_URL + "/{id}", 1L))
                    .andExpect(status().isNotFound());
        }
    }

    private Transaction createTestTransaction(Long id, String desc, TransactionType type) {
        LocalDateTime now = LocalDateTime.now();
        return Transaction.builder()
                .id(id)
                .description(desc)
                .amount(new BigDecimal("100.00"))
                .type(type)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private TransactionVO createTestTransactionVo(Long id, String desc, TransactionType type) {
        LocalDateTime now = LocalDateTime.now();
        return TransactionVO.builder()
                .id(id)
                .description(desc)
                .amount(new BigDecimal("100.00"))
                .type(type)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
