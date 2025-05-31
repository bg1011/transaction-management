package com.hsbc.transaction.performance;


import com.hsbc.transaction.common.enums.TransactionType;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.service.TransactionService;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(value = 1)
@Warmup(iterations = 2)
@Measurement(iterations = 5)
public class TransactionControllerPerformanceTest {

    @Autowired
    private BenchmarkState benchmarkState;

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        private TransactionService transactionService = mock(TransactionService.class);

        private static final String IDEMPOTENCY_KEY = "perf-test-key-123";
        private Transaction mockTransaction;
        private CreateTransactionDTO createTransactionDTO;
        private UpdateTransactionDTO updateTransactionDTO;
        private Pageable pageable;

        @Setup(Level.Trial)
        public void setUp() {
            reset(transactionService); // Reset mock before each trial

            LocalDateTime now = LocalDateTime.now();
            mockTransaction = Transaction.builder()
                    .id(1L)
                    .description("Performance Test Transaction")
                    .amount(new BigDecimal("100.00"))
                    .type(TransactionType.INCOME)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            createTransactionDTO = CreateTransactionDTO.builder()
                    .description("Performance Test Transaction")
                    .amount(new BigDecimal("100.00"))
                    .type(TransactionType.INCOME)
                    .build();

            updateTransactionDTO = UpdateTransactionDTO.builder()
                    .description("Updated Performance Test Transaction")
                    .amount(new BigDecimal("200.00"))
                    .type(TransactionType.EXPENSE)
                    .build();

            pageable = PageRequest.of(0, 10);

            // Configure mock behavior for this benchmark method
            List<TransactionVO> transactionVOs = List.of(TransactionVO.builder()
                    .id(1L).description("Mocked Transaction").amount(BigDecimal.ZERO).type(TransactionType.INCOME)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build());
            Page<TransactionVO> page = new PageImpl<>(transactionVOs);
            when(transactionService.getAllTransactions(any(Pageable.class))).thenReturn(page);

            TransactionVO createdTransactionVO = TransactionVO.builder()
                    .id(2L).description("Created Mock").amount(BigDecimal.TEN).type(TransactionType.EXPENSE)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(transactionService.createTransaction(any(CreateTransactionDTO.class), anyString()))
                    .thenReturn(createdTransactionVO);

            TransactionVO updatedTransactionVO = TransactionVO.builder()
                    .id(1L).description("Updated Mock").amount(BigDecimal.ONE).type(TransactionType.INCOME)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(transactionService.updateTransaction(any(Long.class), any(UpdateTransactionDTO.class)))
                    .thenReturn(updatedTransactionVO);

            Transaction mockTrans = Transaction.builder()
                    .id(1L).description("Found Mock").amount(BigDecimal.valueOf(100)).type(TransactionType.INCOME)
                    .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
            when(transactionService.getTransactionById(any(Long.class)))
                    .thenReturn(Optional.of(mockTrans));
        }

        @Benchmark
        public void testGetAllTransactionsPerformance() {
            // 执行分页查询
            transactionService.getAllTransactions(pageable);
        }

        @Benchmark
        public void testCreateTransactionPerformance() {
            // 执行创建交易
            transactionService.createTransaction(createTransactionDTO, IDEMPOTENCY_KEY);
        }

        @Benchmark
        public void testUpdateTransactionPerformance() {
            // 执行更新交易
            transactionService.updateTransaction(1L, updateTransactionDTO);
        }

        @Benchmark
        public void testGetTransactionByIdPerformance() {
            // 执行查询单个交易
            transactionService.getTransactionById(1L);
        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(TransactionControllerPerformanceTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
} 