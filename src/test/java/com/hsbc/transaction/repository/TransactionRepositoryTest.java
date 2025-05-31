package com.hsbc.transaction.repository;

import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.common.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    private Transaction transaction1;
    private Transaction transaction2;
    private Transaction transaction3;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();

        LocalDateTime now = LocalDateTime.now();
        transaction1 = Transaction.builder()
                .description("Test Transaction 1")
                .amount(new BigDecimal("100.00"))
                .type(TransactionType.INCOME)
                .createdAt(now)
                .updatedAt(now)
                .build();

        transaction2 = Transaction.builder()
                .description("Test Transaction 2")
                .amount(new BigDecimal("200.00"))
                .type(TransactionType.EXPENSE)
                .createdAt(now)
                .updatedAt(now)
                .build();

        transaction3 = Transaction.builder()
                .description("Test Transaction 3")
                .amount(new BigDecimal("300.00"))
                .type(TransactionType.INCOME)
                .createdAt(now)
                .updatedAt(now)
                .build();

        transactionRepository.saveAll(List.of(transaction1, transaction2, transaction3));
    }

    @Test
    public void whenFindAll_thenReturnAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        assertThat(transactions).hasSize(3);
        assertThat(transactions).extracting(Transaction::getDescription)
                .containsExactlyInAnyOrder(
                        "Test Transaction 1",
                        "Test Transaction 2",
                        "Test Transaction 3"
                );
    }

    @Test
    public void whenFindAllWithPageable_thenReturnPagedResult() {
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Page<Transaction> page = transactionRepository.findAll(pageable);

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getTotalPages()).isEqualTo(2);
    }

    @Test
    public void whenFindByType_thenReturnFilteredTransactions() {
        List<Transaction> incomes = transactionRepository.findByType(TransactionType.INCOME);
        assertThat(incomes).hasSize(2);
        assertThat(incomes).extracting(Transaction::getType)
                .containsOnly(TransactionType.INCOME);

        List<Transaction> expenses = transactionRepository.findByType(TransactionType.EXPENSE);
        assertThat(expenses).hasSize(1);
        assertThat(expenses).extracting(Transaction::getType)
                .containsOnly(TransactionType.EXPENSE);
    }

    @Test
    public void whenFindByAmountBetween_thenReturnFilteredTransactions() {
        List<Transaction> transactions = transactionRepository.findByAmountBetween(
                new BigDecimal("150.00"),
                new BigDecimal("250.00")
        );

        assertThat(transactions).hasSize(1);
        assertThat(transactions.get(0).getAmount())
                .isEqualByComparingTo(new BigDecimal("200.00"));
    }
}