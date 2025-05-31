package com.hsbc.transaction.service;

import com.hsbc.transaction.common.enums.TransactionType;
import com.hsbc.transaction.common.exception.BusinessException;
import com.hsbc.transaction.common.exception.ErrorCode;
import com.hsbc.transaction.common.util.IdempotencyUtil;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private IdempotencyUtil idempotencyUtil;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private static final String IDEMPOTENCY_KEY = "test-key-123";

    private Transaction mockTransaction;
    private CreateTransactionDTO createTransactionDTO;
    private UpdateTransactionDTO updateTransactionDTO;

    @BeforeEach
    void setUp() {
        reset(transactionRepository, idempotencyUtil);

        LocalDateTime now = LocalDateTime.now();
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
    @DisplayName("Create Transaction Tests")
    class CreateTransactionTests {

        @Test
        @DisplayName("Should create transaction successfully")
        void whenCreateTransaction_thenReturnSavedTransaction() {
            when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

            TransactionVO result = transactionService.createTransaction(createTransactionDTO, IDEMPOTENCY_KEY);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDescription()).isEqualTo("Test Transaction");
            verify(idempotencyUtil).checkIdempotency(IDEMPOTENCY_KEY);
        }

        @Test
        @DisplayName("Should throw exception for invalid transaction data")
        void whenCreateInvalidTransaction_thenThrowException() {
            CreateTransactionDTO invalidTransaction = CreateTransactionDTO.builder()
                    .amount(BigDecimal.ZERO)
                    .build();

            assertThatThrownBy(() -> transactionService.createTransaction(invalidTransaction, IDEMPOTENCY_KEY))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Update Transaction Tests")
    class UpdateTransactionTests {

        @Test
        @DisplayName("Should update transaction successfully")
        void whenUpdateTransaction_thenReturnUpdatedTransaction() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));
            when(transactionRepository.save(any(Transaction.class))).thenReturn(mockTransaction);

            TransactionVO result = transactionService.updateTransaction(1L, updateTransactionDTO);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getDescription()).isEqualTo(updateTransactionDTO.getDescription());
        }

        @Test
        @DisplayName("Should throw exception when updating non-existing transaction")
        void whenUpdateNonExistingTransaction_thenThrowException() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> transactionService.updateTransaction(1L, updateTransactionDTO))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TRANSACTION_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("Get Transaction Tests")
    class GetTransactionTests {

        @Test
        @DisplayName("Should return transaction when found")
        void whenGetTransactionById_thenReturnTransaction() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.of(mockTransaction));

            Optional<Transaction> result = transactionService.getTransactionById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getDescription()).isEqualTo("Test Transaction");
            assertThat(result.get().getType()).isEqualTo(TransactionType.INCOME);
        }

        @Test
        @DisplayName("Should return empty when transaction not found")
        void whenGetNonExistingTransaction_thenReturnEmpty() {
            when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

            Optional<Transaction> result = transactionService.getTransactionById(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Get All Transactions Tests")
    class GetAllTransactionsTests {

        @Test
        @DisplayName("Should return paginated transactions")
        void whenGetAllTransactions_thenReturnPaginatedList() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Transaction> page = new PageImpl<>(List.of(mockTransaction));
            when(transactionRepository.findAll(pageable)).thenReturn(page);

            Page<TransactionVO> result = transactionService.getAllTransactions(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should return empty page when no transactions exist")
        void whenNoTransactions_thenReturnEmptyPage() {
            Pageable pageable = PageRequest.of(0, 10);
            when(transactionRepository.findAll(pageable)).thenReturn(Page.empty());

            Page<TransactionVO> result = transactionService.getAllTransactions(pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Delete Transaction Tests")
    class DeleteTransactionTests {

        @Test
        @DisplayName("Should delete transaction successfully")
        void whenDeleteTransaction_thenDeleteSuccessfully() {
            when(transactionRepository.existsById(1L)).thenReturn(true);
            doNothing().when(transactionRepository).deleteById(1L);

            transactionService.deleteTransaction(1L);

            verify(transactionRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existing transaction")
        void whenDeleteNonExistingTransaction_thenThrowException() {
            when(transactionRepository.existsById(1L)).thenReturn(false);

            assertThatThrownBy(() -> transactionService.deleteTransaction(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TRANSACTION_NOT_FOUND);
        }
    }
}