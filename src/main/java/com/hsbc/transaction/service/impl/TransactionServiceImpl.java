package com.hsbc.transaction.service.impl;

// Java core imports
import java.util.Arrays;
import java.util.Optional;

// Spring framework imports
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Project imports
import com.hsbc.transaction.common.exception.BusinessException;
import com.hsbc.transaction.common.exception.ErrorCode;
import com.hsbc.transaction.common.util.IdempotencyUtil;
import com.hsbc.transaction.model.dto.request.CreateTransactionDTO;
import com.hsbc.transaction.model.dto.request.UpdateTransactionDTO;
import com.hsbc.transaction.model.dto.response.TransactionVO;
import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.repository.TransactionRepository;
import com.hsbc.transaction.service.TransactionService;

// Lombok imports
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the TransactionService interface.
 * Provides business logic for managing financial transactions with caching and idempotency support.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final IdempotencyUtil idempotencyUtil;

    /**
     * Creates a new transaction with idempotency check.
     * Invalidates the transactions cache after creation.
     *
     * @param createTransactionDTO Transaction data
     * @param idempotencyKey Key for idempotency check
     * @return Created transaction details
     */
    @Override
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO createTransaction(CreateTransactionDTO createTransactionDTO, String idempotencyKey) {
        // Check idempotency
        idempotencyUtil.checkIdempotency(idempotencyKey);

        if (createTransactionDTO == null) {
            throw new IllegalArgumentException("Transaction parameters cannot be empty");
        }
        if (createTransactionDTO.getDescription() == null || createTransactionDTO.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Transaction description cannot be empty");
        }
        if (createTransactionDTO.getAmount() == null) {
            throw new IllegalArgumentException("Transaction amount cannot be empty");
        }
        if (createTransactionDTO.getType() == null) {
            throw new IllegalArgumentException("Transaction type cannot be empty");
        }

        Transaction transaction = Transaction.builder()
                .description(createTransactionDTO.getDescription())
                .amount(createTransactionDTO.getAmount())
                .type(createTransactionDTO.getType())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction created: {}", savedTransaction.getId());

        return convertToVO(savedTransaction);
    }

    /**
     * Updates an existing transaction.
     * Invalidates the transactions cache after update.
     *
     * @param id Transaction ID
     * @param updateTransactionDTO Updated transaction data
     * @return Updated transaction details
     * @throws BusinessException if transaction not found
     */
    @Override
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public TransactionVO updateTransaction(Long id, UpdateTransactionDTO updateTransactionDTO) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (updateTransactionDTO.getDescription() != null) {
            transaction.setDescription(updateTransactionDTO.getDescription());
        }
        if (updateTransactionDTO.getAmount() != null) {
            transaction.setAmount(updateTransactionDTO.getAmount());
        }
        if (updateTransactionDTO.getType() != null) {
            transaction.setType(updateTransactionDTO.getType());
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Transaction updated: {}", id);

        return convertToVO(updatedTransaction);
    }

    /**
     * Retrieves a transaction by its ID.
     *
     * @param id Transaction ID
     * @return Transaction details if found
     */
    @Override
    @Transactional(readOnly = true)
    public TransactionVO findById(Long id) {
        return transactionRepository.findById(id)
                .map(this::convertToVO)
                .orElseThrow(() -> new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND));
    }

    /**
     * Deletes a transaction by its ID.
     * Invalidates the transactions cache after deletion.
     *
     * @param id Transaction ID
     * @throws BusinessException if transaction not found
     */
    @Override
    @Transactional
    @CacheEvict(value = "transactions", allEntries = true)
    public void deleteTransaction(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.TRANSACTION_NOT_FOUND);
        }
        transactionRepository.deleteById(id);
        log.info("Transaction deleted: {}", id);
    }

    /**
     * Retrieves a paginated list of transactions.
     * Results are cached for better performance.
     *
     * @param pageable Pagination and sorting information
     * @return Paginated list of transactions
     */
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "transactions")
    public Page<TransactionVO> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable)
                .map(this::convertToVO);
    }

    /**
     * Retrieves a transaction entity by its ID.
     *
     * @param id Transaction ID
     * @return Optional containing the transaction if found
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * Converts a Transaction entity to a TransactionVO.
     *
     * @param transaction Transaction entity
     * @return TransactionVO object
     */
    private TransactionVO convertToVO(Transaction transaction) {
        return TransactionVO.builder()
                .id(transaction.getId())
                .description(transaction.getDescription())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

    /**
     * Validates if a sort property is valid for the Transaction entity.
     *
     * @param property The property name to validate
     * @return true if the property is valid, false otherwise
     */
    private boolean isValidSortProperty(String property) {
        try {
            return Arrays.stream(Transaction.class.getDeclaredFields())
                    .anyMatch(field -> field.getName().equals(property));
        } catch (Exception e) {
            return false;
        }
    }
}
