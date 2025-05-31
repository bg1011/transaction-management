package com.hsbc.transaction.service;

import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TransactionService {
    TransactionVO createTransaction(CreateTransactionDTO createTransactionDTO, String idempotencyKey);
    TransactionVO updateTransaction(Long id, UpdateTransactionDTO updateTransactionDTO);
    TransactionVO findById(Long id);
    void deleteTransaction(Long id);

    Page<TransactionVO> getAllTransactions(Pageable pageable);
    Optional<Transaction> getTransactionById(Long id);
} 