package com.hsbc.transaction.repository;

import com.hsbc.transaction.model.entity.Transaction;
import com.hsbc.transaction.common.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for managing Transaction entities in the database.
 * This interface extends JpaRepository to provide basic CRUD operations and
 * adds custom query methods for transaction-specific operations.
 *
 * <p>The repository provides methods for:
 * <ul>
 *     <li>Finding transactions by type</li>
 *     <li>Finding transactions by amount range</li>
 *     <li>Finding transactions with pagination</li>
 *     <li>Combined queries with multiple criteria</li>
 * </ul>
 * </p>
 *
 * @author HSBC Development Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Finds all transactions of the specified type.
     *
     * @param type the transaction type (INCOME or EXPENSE)
     * @return a list of transactions matching the specified type
     * @throws IllegalArgumentException if type is null
     */
    List<Transaction> findByType(TransactionType type);

    /**
     * Finds all transactions within the specified amount range.
     *
     * @param minAmount the minimum amount (inclusive)
     * @param maxAmount the maximum amount (inclusive)
     * @return a list of transactions within the specified amount range
     * @throws IllegalArgumentException if minAmount is greater than maxAmount
     */
    List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Finds all transactions of the specified type with pagination support.
     *
     * @param type the transaction type (INCOME or EXPENSE)
     * @param pageable pagination information including page number, size, and sorting
     * @return a page of transactions matching the specified type
     * @throws IllegalArgumentException if type is null or pageable is null
     */
    Page<Transaction> findByType(String type, Pageable pageable);

    /**
     * Finds all transactions within the specified amount range with pagination support.
     *
     * @param minAmount the minimum amount (inclusive)
     * @param maxAmount the maximum amount (inclusive)
     * @param pageable pagination information including page number, size, and sorting
     * @return a page of transactions within the specified amount range
     * @throws IllegalArgumentException if minAmount is greater than maxAmount or pageable is null
     */
    Page<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount, Pageable pageable);

    /**
     * Finds all transactions matching both type and amount range criteria with pagination support.
     *
     * @param type the transaction type (INCOME or EXPENSE)
     * @param minAmount the minimum amount (inclusive)
     * @param maxAmount the maximum amount (inclusive)
     * @param pageable pagination information including page number, size, and sorting
     * @return a page of transactions matching all specified criteria
     * @throws IllegalArgumentException if type is null, minAmount is greater than maxAmount, or pageable is null
     */
    Page<Transaction> findByTypeAndAmountBetween(
            String type,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            Pageable pageable);

    /**
     * This repository inherits the following methods from JpaRepository:
     * <ul>
     *     <li>{@link JpaRepository#save(Object)} - Save a transaction</li>
     *     <li>{@link JpaRepository#findById(Object)} - Find a transaction by ID</li>
     *     <li>{@link JpaRepository#findAll()} - Find all transactions</li>
     *     <li>{@link JpaRepository#deleteById(Object)} - Delete a transaction by ID</li>
     *     <li>{@link JpaRepository#count()} - Count total transactions</li>
     * </ul>
     */
}
